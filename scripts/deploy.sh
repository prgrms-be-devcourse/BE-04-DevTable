if [ "$DEPLOYMENT_GROUP_NAME" == "devtable-user" ]
then
  REPOSITORY=/home/dev-table/user
  APP_NAME=dev-table/user
elif [ "$DEPLOYMENT_GROUP_NAME" == "devtable-owner" ]
then
  REPOSITORY=/home/dev-table/owner
  APP_NAME=dev-table/owner
else
  echo "Unknown DEPLOYMENT_GROUP_NAME: $DEPLOYMENT_GROUP_NAME"
  exit 1
fi

echo "현재 이동 된 REPOSITORY :  $REPOSITORY"

JAR_NAME=$(ls $REPOSITORY/build/libs/*.jar | grep '.jar' | tail -n 1)

CURRENT_PID=$(pgrep -f "$APP_NAME")

if [ -z "$CURRENT_PID" ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 "$CURRENT_PID"
  sleep 5
fi

echo "JAR_NAME : > $JAR_NAME"
echo "> $JAR_PATH에 실행권한 추가"
chmod +x $JAR_NAME

echo "> $JAR_NAME 배포"

chmod 755 nohup

nohup java -jar \
        -Dspring.profiles.active=dev \
        "$JAR_NAME" > nohup.out 2>&1 &