# BAW Accelerator for Process Mining 

## Purpose

Please find a small docker to help you enhance the data from your IBM Business Automation Workflow.
The purpose is to generate a CSV file that IBM Process Mining can ingest. 

## How to use this image

Run the docker: 

    docker run -p 8080:8089 baw-accelerator

Connect to the portal using the URL: 

    http://localhost:8080

### 1. Generate a query to create a raw input

Generate a SQL query to extract the event from your database history.
Please export the result into a CSV format.

The <b> "CH_Content"</b> column is a BLOB object that can be extracted using HEX, BASE64 

 
### 2. Augment the CSV 

Follow the wizard to augment your CSV with additional properties coming from your case application.

