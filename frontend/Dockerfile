# Frontend als container: eerst bouwen met Node, daarna de statische bestanden serveren met nginx.
FROM node:24 AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
# Het adres van de API wordt bij het bouwen in de pagina gebakken (Vite leest VITE_API_URL tijdens `npm run build`).
ARG VITE_API_URL
ENV VITE_API_URL=$VITE_API_URL
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
