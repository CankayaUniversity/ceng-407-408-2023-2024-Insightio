# Use a base image that includes Node.js
FROM node:latest

# Install necessary system libraries for Electron
RUN apt-get update && apt-get install -y \
    libnss3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libgtk-3-0 \
    libdrm2 \
    libgbm1 \
    libasound2 \
    && rm -rf /var/lib/apt/lists/*

# Install Electron globally
RUN npm install -g electron@latest

# Set the working directory inside the container
WORKDIR /app

# Copy the application code into the container
COPY . .

# Install dependencies using yarn
RUN yarn

# Expose the port used by the application (if necessary)
EXPOSE 5173

# Set the entry point to run the development server
CMD ["yarn", "dev"]
