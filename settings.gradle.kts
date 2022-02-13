plugins {
    id("de.fayard.refreshVersions") version "0.40.0"
////                            # available:"0.40.1"
}

include(
    ":example", ":example-compose", ":stagestepbar", ":stagestepbar-compose", ":snapshot-test"
)
