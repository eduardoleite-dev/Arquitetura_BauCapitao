$ErrorActionPreference = 'Stop'

$organization = 'https://dev.azure.com/eduardoleite0637'
$project = 'ai'
$feed = 'ArquiteturaBauCapitao'

if (-not (Get-Command az -ErrorAction SilentlyContinue)) {
    throw 'Azure CLI nao encontrado. Instale o Azure CLI antes de continuar.'
}

az extension add --name azure-devops --only-show-errors

if (-not $env:AZURE_ARTIFACTS_TOKEN) {
    throw 'Defina AZURE_ARTIFACTS_TOKEN com um PAT do Azure DevOps antes de publicar.'
}

$env:AZURE_DEVOPS_EXT_PAT = $env:AZURE_ARTIFACTS_TOKEN
az devops configure --defaults organization=$organization project=$project

$existingFeed = az artifacts feed show --feed $feed --project $project --organization $organization --query name --output tsv 2>$null
if (-not $existingFeed) {
    az artifacts feed create --name $feed --project $project --organization $organization --output none
}

$env:AZURE_ARTIFACTS_FEED_URL = "https://pkgs.dev.azure.com/eduardoleite0637/ai/_packaging/$feed/maven/v1"
.\gradlew.bat clean test publish
