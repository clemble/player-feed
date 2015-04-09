FROM java:8-jre
MAINTAINER antono@clemble.com

EXPOSE 10011

ADD target/player-feed-0.17.0-SNAPSHOT.jar /data/player-feed.jar

CMD java -jar -Dspring.profiles.active=cloud -Dserver.port=10011 /data/player-feed.jar
