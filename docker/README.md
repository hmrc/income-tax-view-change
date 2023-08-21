-- Building image:

   docker build -t sbt_linux_v5 .
   
-- Running image:

   docker run -it --cpu-period=100000 --cpu-quota=50000 -v /Users/hmrc/devcore/itvc/income-tax-view-change:/income-tax-view-change sbt_linux_v5
   
-- Run unit tests within docker container:
   
   cd /income-tax-view-change
   sbt clean compile test
