FROM amazonlinux

RUN yum install -y java-11-amazon-corretto  
RUN curl -L https://www.scala-sbt.org/sbt-rpm.repo > /etc/yum.repos.d/sbt-rpm.repo
RUN yum install -y sbt-1.3.13
RUN yum install -y git
RUN sbt update
