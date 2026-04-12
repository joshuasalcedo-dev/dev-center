use tauri_plugin_shell::ShellExt;
use tauri_plugin_shell::process::CommandEvent;
use std::sync::Arc;
use tauri::{Manager, State};
use tokio::sync::OnceCell;

struct SidecarPort(Arc<OnceCell<u16>>);

const LOCAL_SERVER_PORT_DEV: u16 = 5050;
const REMOTE_SERVER_URL_DEV: &str = "http://localhost:8080";
const REMOTE_SERVER_URL_PROD: &str = "https://api-commandcenter.joshuasalcedo.io";

#[tauri::command]
async fn get_sidecar_port(state: State<'_, SidecarPort>) -> Result<u16, String> {
  if cfg!(debug_assertions) {
    return Ok(LOCAL_SERVER_PORT_DEV);
  }
  state.0
    .get()
    .copied()
    .ok_or_else(|| "sidecar not ready yet".into())
}

#[tauri::command]
async fn get_sidecar_url(state: State<'_, SidecarPort>) -> Result<String, String> {
  if cfg!(debug_assertions) {
    return Ok(format!("http://localhost:{}", LOCAL_SERVER_PORT_DEV));
  }
  state.0
    .get()
    .map(|port| format!("http://localhost:{}", port))
    .ok_or_else(|| "sidecar not ready yet".into())
}

#[tauri::command]
fn get_remote_server_url() -> String {
  if cfg!(debug_assertions) {
    REMOTE_SERVER_URL_DEV.to_string()
  } else {
    REMOTE_SERVER_URL_PROD.to_string()
  }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
  tauri::Builder::default()
    .plugin(tauri_plugin_shell::init())
    .manage(SidecarPort(Arc::new(OnceCell::new())))
    .invoke_handler(tauri::generate_handler![get_sidecar_port, get_sidecar_url, get_remote_server_url])
    .setup(|app| {
      if cfg!(debug_assertions) {
        app.handle().plugin(
          tauri_plugin_log::Builder::default()
            .level(log::LevelFilter::Info)
            .build(),
        )?;
      }

      // In prod: launch the native sidecar binary bundled via externalBin
      if !cfg!(debug_assertions) {
        let sidecar = app.shell().sidecar("devcom-server").unwrap();
        let (mut rx, _child) = sidecar.spawn().expect("failed to spawn sidecar");

        let port_cell = app.state::<SidecarPort>().0.clone();
        tauri::async_runtime::spawn(async move {
          while let Some(event) = rx.recv().await {
            if let CommandEvent::Stdout(line) = &event {
              let line = String::from_utf8_lossy(line);
              if let Some(port_str) = line.strip_prefix("SIDECAR_PORT=") {
                if let Ok(port) = port_str.trim().parse::<u16>() {
                  let _ = port_cell.set(port);
                  println!("Sidecar started on port {}", port);
                }
              }
            }
          }
        });
      }

      Ok(())
    })
    .run(tauri::generate_context!())
    .expect("error while running tauri application");
}
