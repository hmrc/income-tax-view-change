import play.core.PlayVersion
import play.sbt.PlayImport
import play.sbt.routes.RoutesKeys
import sbt.*
import uk.gov.hmrc.DefaultBuildSettings.*
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "income-tax-view-change"

val bootstrapPlayVersion = "10.2.0"
val mockitoVersion = "5.18.0"
val wiremockVersion = "3.0.0-beta-7"
val scalaMockVersion = "7.5.0"
val jsoupVersion = "1.21.1"
val currentScalaVersion = "3.3.6"

val compile: Seq[ModuleID] = Seq(
  PlayImport.ws,
  "uk.gov.hmrc" %% "bootstrap-backend-play-30"   % bootstrapPlayVersion
)

def test(scope: String = "test"): Seq[ModuleID] = Seq(
  "org.scalamock" %% "scalamock" % scalaMockVersion % scope,
  "org.jsoup" % "jsoup" % jsoupVersion % scope,
  "uk.gov.hmrc" %% "bootstrap-test-play-30" % bootstrapPlayVersion % scope,
  "org.mockito" % "mockito-core" % mockitoVersion % scope,
  "com.github.tomakehurst" % "wiremock" % wiremockVersion % scope,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.17.1",
  "org.scalatest"       %% "scalatest"              % "3.2.19" % scope,
    caffeine
)

def it(scope: String = "test"): Seq[ModuleID] = Seq(
  "org.scalamock" %% "scalamock" % scalaMockVersion % scope,
  "org.jsoup" % "jsoup" % jsoupVersion % scope,
  "org.mockito" % "mockito-core" % mockitoVersion % scope,
  "com.github.tomakehurst" % "wiremock" % wiremockVersion % scope,
  "org.scalatest"       %% "scalatest"              % "3.2.19",
  caffeine
)

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val appDependenciesIt: Seq[ModuleID] = it()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;uk.gov.hmrc.BuildInfo;models\\.data\\..*;app.*;prod.*;config.*;com.*;testOnly.*;testOnlyDoNotUseInAppConf.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 80,
    ScoverageKeys.coverageFailOnMinimum := true,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(scalaVersion := currentScalaVersion)
  .settings(scoverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(majorVersion := 1)
  .settings(RoutesKeys.routesImport -= "controllers.Assets.Asset")
  .settings(scalacOptions += "-Xfatal-warnings")
  .settings(scalacOptions += "-deprecation:false")
  .settings(
    libraryDependencies ++= appDependencies,
    retrieveManaged := true
  )
  .settings(
    Test / Keys.fork := true,
    scalaVersion := currentScalaVersion,
    scalacOptions += "-Wconf:src=routes/.*:s",
    Test / javaOptions += "-Dlogger.resource=logback-test.xml")
  .settings(
    Keys.fork := false)
  .settings(resolvers ++= Seq(
    MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2"),
      Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)
  ))
  .settings(ThisBuild / scalacOptions += "-Wconf:msg=Flag.*repeatedly:s")
  .settings(
    scalacOptions --= Seq("-Wunused", "-Wunused:all"),
    scalacOptions += "-deprecation",
    Test / scalacOptions ++= Seq(
      "-Wunused:imports",
      "-Wunused:params",
      "-Wunused:implicits",
      "-Wunused:explicits",
      "-Wunused:privates"
    ),
  )

lazy val it = project
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings(true).head)
  .enablePlugins(play.sbt.PlayScala)
  .settings(
    publish / skip := true
  )
  .settings(scalaVersion := currentScalaVersion)
  .settings(majorVersion := 1)
  .settings(
    testForkedParallel := true
  )
  .settings(
    libraryDependencies ++= appDependenciesIt
  )
  .settings(ThisBuild / scalacOptions += "-Wconf:msg=Flag.*repeatedly:s")