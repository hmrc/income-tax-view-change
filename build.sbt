import play.core.PlayVersion
import play.sbt.routes.RoutesKeys
import sbt._
import uk.gov.hmrc.DefaultBuildSettings._
import uk.gov.hmrc.versioning.SbtGitVersioning.autoImport.majorVersion

val appName = "income-tax-view-change"

val bootstrapPlayVersion = "5.21.0"
val bootstrapBackEndPlayVersion = "5.21.0"
val mockitoVersion = "3.12.4"
val wiremockVersion = "2.26.3"
val scalaMockVersion = "5.2.0"
val pegdownVersion = "1.6.0"
val jsoupVersion = "1.11.3"
val scalaTestPlusVersion = "5.0.0"

val compile: Seq[ModuleID] = Seq(
  ws,
  "uk.gov.hmrc" %% "bootstrap-backend-play-28" % bootstrapPlayVersion
)

def test(scope: String = "test,it"): Seq[ModuleID] = Seq(
  "org.scalatestplus.play" %% "scalatestplus-play" % scalaTestPlusVersion % scope,
  "org.scalamock" %% "scalamock" % scalaMockVersion % scope,
  "org.pegdown" % "pegdown" % pegdownVersion % scope,
  "org.jsoup" % "jsoup" % jsoupVersion % scope,
  "com.typesafe.play" %% "play-test" % PlayVersion.current % scope,
  "org.mockito" % "mockito-core" % mockitoVersion % scope,
  "com.github.tomakehurst" % "wiremock-jre8" % wiremockVersion % scope,
  "uk.gov.hmrc" %% "bootstrap-test-play-28" % bootstrapPlayVersion % scope,
  caffeine
)

lazy val appDependencies: Seq[ModuleID] = compile ++ test()
lazy val plugins: Seq[Plugins] = Seq.empty
lazy val playSettings: Seq[Setting[_]] = Seq.empty

lazy val scoverageSettings = {
  import scoverage.ScoverageKeys
  Seq(
    // Semicolon-separated list of regexs matching classes to exclude
    ScoverageKeys.coverageExcludedPackages := "<empty>;Reverse.*;uk.gov.hmrc.BuildInfo;models\\.data\\..*;app.*;prod.*;config.*;com.*;testOnly.*;testOnlyDoNotUseInAppConf.*;",
    ScoverageKeys.coverageMinimumStmtTotal := 90,
    ScoverageKeys.coverageFailOnMinimum := false,
    ScoverageKeys.coverageHighlighting := true
  )
}

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin)
  .settings(playSettings: _*)
  .settings(scalaSettings: _*)
  .settings(scoverageSettings: _*)
  .settings(defaultSettings(): _*)
  .settings(majorVersion := 1)
  .settings(RoutesKeys.routesImport -= "controllers.Assets.Asset")
  .settings(
    libraryDependencies ++= appDependencies,
    retrieveManaged := true,
    update / evictionWarningOptions := EvictionWarningOptions.default.withWarnScalaVersionEviction(warnScalaVersionEviction = false)
  )
  .settings(
    Test / Keys.fork := true,
    scalaVersion := "2.13.8",
    scalacOptions += "-Wconf:src=routes/.*:s",
    Test / javaOptions += "-Dlogger.resource=logback-test.xml")
  .configs(IntegrationTest)
  .settings(inConfig(IntegrationTest)(Defaults.itSettings): _*)
  .settings(
    IntegrationTest / Keys.fork := false,
    IntegrationTest / unmanagedSourceDirectories := (IntegrationTest / baseDirectory) (base => Seq(base / "it")).value,
    addTestReportOption(IntegrationTest, "int-test-reports"),
    IntegrationTest / parallelExecution := false)
  .settings(resolvers ++= Seq(
    MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2"),
      Resolver.url("HMRC-open-artefacts-ivy", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns),
    Resolver.jcenterRepo
  ))