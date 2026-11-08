$ErrorActionPreference = "Stop"

# compila el proyecto y copia las dependencias a target/lib
& mvn compile

if ($LASTEXITCODE -ne 0) {
    exit $LASTEXITCODE
}

# ejecuta con las aserciones activas; target/lib trae FlatLaf y MigLayout
& java -ea -cp "target/classes;target/lib/*" view.Main
exit $LASTEXITCODE