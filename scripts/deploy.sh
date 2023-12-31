if [ "$DEPLOYMENT_GROUP_NAME" == "devtable-user" ]
then
  JAR_NAME=$(ls /home/dev-table/user/build/libs/*.jar | grep 'user-' | tail -n 1)
  echo "발견한 jar 이름 > $JAR_NAME"
elif [ "$DEPLOYMENT_GROUP_NAME" == "devtable-owner" ]
then
  JAR_NAME=$(ls /home/dev-table/owner/build/libs/*.jar | grep 'owner-' | tail -n 1)
  echo "발견한 jar 이름 > $JAR_NAME"
elif [ "$DEPLOYMENT_GROUP_NAME" == "devtable-alarm" ]
then
  JAR_NAME=$(ls /home/dev-table/alarm/build/libs/*.jar | grep 'alarm-' | tail -n 1)
  echo "발견한 jar 이름 > $JAR_NAME"
else
  echo "Unknown DEPLOYMENT_GROUP_NAME: $DEPLOYMENT_GROUP_NAME"
  exit 1
fi

CURRENT_PID=$(pgrep -f "$JAR_NAME")

if [ -z "$CURRENT_PID" ]
then
  echo "> CURRENT_PID : $CURRENT_PID"
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> CURRENT_PID : $CURRENT_PID"
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 "$CURRENT_PID"
  sleep 5
fi

echo "JAR_NAME : > $JAR_NAME"
chmod +x $JAR_NAME

echo "> $JAR_NAME 배포"

if [ "$DEPLOYMENT_GROUP_NAME" == "devtable-user" ]
then
  nohup java -jar \
          -Dspring.profiles.active=dev \
          -Dspring.config.location=/home/yml/user/application.yml,/home/yml/user/application-dev.yml \
          "$JAR_NAME" > /home/log/user/nohup_log.out 2>&1 &
elif [ "$DEPLOYMENT_GROUP_NAME" == "devtable-owner" ]
then
  nohup java -jar \
            -Dspring.profiles.active=dev \
            -Dspring.config.location=/home/yml/owner/application.yml,/home/yml/owner/application-dev.yml \
            "$JAR_NAME" > /home/log/owner/nohup_log.out 2>&1 &
elif [ "$DEPLOYMENT_GROUP_NAME" == "devtable-alarm" ]
then
  nohup java -jar \
              -Dspring.profiles.active=dev \
              -Dspring.config.location=/home/yml/alarm/application.yml \
              "$JAR_NAME" > /home/log/alarm/nohup_log.out 2>&1 &
else
  echo "Unknown DEPLOYMENT_GROUP_NAME: $DEPLOYMENT_GROUP_NAME"
  exit 1
fi