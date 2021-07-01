FROM maven:3.6.3-ibmjava-8-alpine AS agentCampaignServiceBuild
ARG STAGE=dev
WORKDIR /usr/src/agentCampaignService
COPY . .
RUN mvn package -P${STAGE} -DskipTests


FROM openjdk:8-jdk-alpine AS agentCampaignServiceRuntime
WORKDIR /app
COPY --from=agentCampaignServiceBuild /usr/src/agentCampaignService/target/agentCampaignService.jar ./
EXPOSE 8080
CMD java -jar agentCampaignService.jar