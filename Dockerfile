FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 8080

ADD target/player-feed-0.17.0-SNAPSHOT.jar /data/player-feed.jar

CMD java -jar -Dspring.profiles.active=cloud /data/player-feed.jar
