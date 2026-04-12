"use client"

import { useEffect, useState } from "react"

export default function AuthSuccessPage() {
  const [closing, setClosing] = useState(false)

  useEffect(() => {
    const timer = setTimeout(() => {
      setClosing(true)
      window.close()
    }, 2000)
    return () => clearTimeout(timer)
  }, [])

  return (
    <div className="flex min-h-screen items-center justify-center">
      <div className="text-center space-y-4">
        <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-full bg-green-500/10">
          <svg
            className="h-8 w-8 text-green-500"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth={2}
            stroke="currentColor"
          >
            <path strokeLinecap="round" strokeLinejoin="round" d="M4.5 12.75l6 6 9-13.5" />
          </svg>
        </div>
        <h1 className="text-2xl font-semibold tracking-tight">Signed in successfully</h1>
        <p className="text-muted-foreground">
          {closing
            ? "Closing this tab..."
            : "You can return to the desktop app. This tab will close automatically."}
        </p>
      </div>
    </div>
  )
}
