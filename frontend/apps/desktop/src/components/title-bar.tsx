import { getCurrentWindow } from "@tauri-apps/api/window"
import { Minus, Square, X } from "lucide-react"

const appWindow = getCurrentWindow()

export function Titlebar() {
  return (
    <div
      data-tauri-drag-region
      className="flex h-9 items-center justify-between border-b border-border bg-background select-none"
    >
      <div data-tauri-drag-region className="flex items-center gap-2 pl-3">
        <span className="text-xs font-semibold tracking-tight text-foreground">
          dev-center
        </span>
      </div>

      <div className="flex h-full">
        <button
          onClick={() => appWindow.minimize()}
          className="inline-flex h-full w-11 items-center justify-center text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
        >
          <Minus className="size-3.5" />
        </button>
        <button
          onClick={() => appWindow.toggleMaximize()}
          className="inline-flex h-full w-11 items-center justify-center text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
        >
          <Square className="size-3" />
        </button>
        <button
          onClick={() => appWindow.close()}
          className="inline-flex h-full w-11 items-center justify-center text-muted-foreground transition-colors hover:bg-destructive hover:text-white"
        >
          <X className="size-3.5" />
        </button>
      </div>
    </div>
  )
}
