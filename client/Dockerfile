# Install
FROM node:latest as dependency
WORKDIR /app
COPY package*.json ./
RUN npm install

# Build
FROM dependency as build
ARG REACT_APP_API_ENDPOINT
ENV REACT_APP_API_ENDPOINT=$REACT_APP_API_ENDPOINT
WORKDIR /app
COPY . .
RUN npm run build

# Serve the front app using nginx
FROM nginx:alpine as server
COPY --from=build /app/build /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]