@('authenticated','admin','public') | ForEach-Object {
    Invoke-WebRequest -Uri "http://localhost:8080/v3/api-docs/$_" -OutFile "openapi-$_.json"
}