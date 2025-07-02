mvn test && \
rm -rf docker/nginx/html/jacoco/ || true && \
cp -r uniproject/target/site/jacoco/ docker/nginx/html/ && \
docker compose -f docker/nginx/docker-compose.yml up -d --force-recreate
