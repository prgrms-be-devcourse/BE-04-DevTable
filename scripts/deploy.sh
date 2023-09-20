if [ "$DEPLOYMENT_GROUP_NAME" == "devtable-user" ]
then
  REPOSITORY=/home/dev-table/user
elif [ "$DEPLOYMENT_GROUP_NAME" == "devtable-owner" ]
then
  REPOSITORY=/home/dev-table/owner
else
  echo "Unknown DEPLOYMENT_GROUP_NAME: $DEPLOYMENT_GROUP_NAME"
  exit 1
fi

cd "$REPOSITORY" || exit 1

echo "현재 이동 된 REPOSITORY :  $REPOSITORY"

APP_NAME=dev-table
JAR_NAME=$(ls build/libs/*.jar | grep '.jar' | tail -n 1)
JAR_PATH=build/libs/$JAR_NAME

CURRENT_PID=$(pgrep -f "$APP_NAME")

if [ -z "$CURRENT_PID" ]
then
  echo "> 현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "> kill -15 $CURRENT_PID"
  sudo kill -15 "$CURRENT_PID"
  sleep 5
fi

echo "> $JAR_PATH 배포"

nohup java -jar \
        -Dspring.profiles.active=dev \
        "$JAR_PATH" > nohup.out 2>&1 &