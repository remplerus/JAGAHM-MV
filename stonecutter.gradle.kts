plugins {
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
    id("me.modmuss50.mod-publish-plugin") version "1.1.+" apply false // Publishes builds to hosting websites
}

stonecutter active "26.2"

// Make newer versions be published last
stonecutter tasks {
    order("publishModrinth")
    order("publishCurseforge")
}

// See https://stonecutter.kikugie.dev/wiki/config/params
stonecutter parameters {
    swaps["mod_version"] = "\"" + property("mod.version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    constants["release"] = property("mod.id") != "template"
    dependencies["fapi"] = node.project.property("deps.fabric_api") as String

    replacements {
        string(current.parsed > "26.0") {
            replace("classTweaker v1 named", "classTweaker v1 official")
        }
    }
}
