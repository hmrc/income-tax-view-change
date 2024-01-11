

resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += MavenRepository("HMRC-open-artefacts-maven2", "https://open.artefacts.tax.service.gov.uk/maven2")
resolvers += Resolver.url("HMRC-open-artefacts-ivy2", url("https://open.artefacts.tax.service.gov.uk/ivy2"))(Resolver.ivyStylePatterns)

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "3.17.0")

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "2.4.0")

addSbtPlugin("org.scoverage" % "sbt-scoverage" % "2.0.9")

// TODO: decommission plugin???
//addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.11.1")

addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.0")

//addSbtPlugin("io.github.irundaia" % "sbt-sassify" % "1.5.2")