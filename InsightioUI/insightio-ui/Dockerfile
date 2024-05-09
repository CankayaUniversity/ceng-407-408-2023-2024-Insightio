# Use a base image that includes Node.js and Electron dependencies
FROM node:latest

# Install libnss3, libatk1.0-0, and libatk-bridge2.0-0
RUN apt-get update && \
    apt-get install -y libnss3 libatk1.0-0 libatk-bridge2.0-0 && \
    rm -rf /var/lib/apt/lists/*

# Set the working directory inside the container
WORKDIR /app

# Copy the application code into the container
COPY . .

# Install dependencies using yarn
RUN yarn

# Expose the port used by the application (if necessary)
# EXPOSE <port>

# Set the entry point to run the development server
CMD ["yarn", "dev"]
