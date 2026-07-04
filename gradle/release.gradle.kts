import buildlogic.release.*
import buildlogic.flavors.*

//TODO 增加gradle release flavor任务，build all，release移动到指定目录，all移动到指定目录
//TODO 增加github/workflows/release.yml

registerReleaseTask(
    taskName = "releaseWasmJs",
    moduleName = ":app:webApp",
    moduleTask = "wasmJsBrowserDistribution",
    artifactRelativePath = "dist/wasmJs/productionExecutable",
    shouldPackage = true,
    renameTo = "SortRss-wasmJs-0.0.1",
    envVars = emptyList()
)