# 🌦️ Weather Data Pipeline — Kafka • Spring Boot • MongoDB • Elasticsearch • Kibana

A real-time distributed data pipeline that collects weather data for multiple cities every 30 minutes, processes it through Kafka, stores raw data in MongoDB, indexes transformed data in Elasticsearch, and visualizes insights through Kibana dashboards.
--
## 🚀 Architecture Overview

##  ✨ Features

✔ Fetch weather data for multiple cities
✔ Kafka-based decoupled ingestion pipeline
✔ Raw JSON storage in MongoDB
✔ Structured indexing into Elasticsearch
✔ Kibana analytics dashboards + map visualizations
✔ Uses polymorphic DTO architecture
✔ Fault-tolerant, scalable, and easily extendable

## 🧩 Project Modules
**1. Weather Producer (Spring Boot)**

  - Fetches data from external Weather API every 30 minutes
  - Publishes JSON payloads to a Kafka topic
    
**2. Forecast Producer (Spring Boot)**

  - Fetches the Forecast weather data for each city exactly at the time of the start of the day (00:00 AM)
  - Store it in a kafka topic
    
**3. Current Weather Consumer**

  - Consumes Kafka events
  - Converts raw JSON to strongly typed DTOs
  - Stores unmodified API payloads into MongoDB as Document
  - Useful for deep analytics, recovery, reprocessing
  - Simultaneously index the processed into Elasticsearch
The same operation is done for the forecasted data every 24hrs by the ForecastConsumer.java

**4. Visualization**

Kibana dashboards for:
  - Temperature trends
  - Multi-city weather comparison
  - Geographic heatmaps
  - Forecast vs actual variation

## 🏗️ Data Model Overview
Raw Data (MongoDB)

Stored as org.bson.Document (no DTO mapping required).

Processed / Indexed Data (Elasticsearch)
