use tauri::Manager;
use tauri_plugin_shell::ShellExt;

#[tauri::command]
fn get_local_server_port(state: tauri::State<'_, LocalServerPort>) -> u16 {
    state.0
}

struct LocalServerPort(u16);

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .plugin(tauri_plugin_clipboard_manager::init())
        .plugin(tauri_plugin_dialog::init())
        .plugin(tauri_plugin_fs::init())
        .plugin(tauri_plugin_global_shortcut::Builder::new().build())
        .plugin(tauri_plugin_http::init())
        .plugin(tauri_plugin_notification::init())
        .plugin(tauri_plugin_opener::init())
        .plugin(tauri_plugin_os::init())
        .plugin(tauri_plugin_process::init())
        .plugin(tauri_plugin_shell::init())
        .plugin(tauri_plugin_store::Builder::new().build())
        .invoke_handler(tauri::generate_handler![get_local_server_port])
        .setup(|app| {
            #[cfg(desktop)]
            app.handle()
                .plugin(tauri_plugin_updater::Builder::new().build())?;

            if cfg!(debug_assertions) {
                app.handle().plugin(
                    tauri_plugin_log::Builder::default()
                        .level(log::LevelFilter::Info)
                        .build(),
                )?;
                // Dev: local-server runs separately on port 5050
                app.manage(LocalServerPort(5050));
            } else {
                // Prod: spawn local-server as sidecar with random port
                let port = find_available_port();
                let sidecar = app
                    .shell()
                    .sidecar("local-server")
                    .expect("failed to create sidecar command")
                    .args(["--server.port", &port.to_string()]);

                let (_rx, _child) = sidecar
                    .spawn()
                    .expect("failed to spawn local-server sidecar");

                log::info!("Local server sidecar started on port {}", port);
                app.manage(LocalServerPort(port));
            }

            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}

fn find_available_port() -> u16 {
    std::net::TcpListener::bind("127.0.0.1:0")
        .expect("failed to bind to find available port")
        .local_addr()
        .expect("failed to get local addr")
        .port()
}
