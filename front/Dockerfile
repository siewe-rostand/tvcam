FROM node:22.0-alpine AS build
WORKDIR /app
COPY package.json ./
RUN npm install
RUN npx gcc --properties es2023 browser module main --first-only --create-ivy-entry-points
COPY . .
RUN npm run build

FROM nginx:stable
COPY --from=build /app/dist/front/ /usr/share/nginx/html
EXPOSE 80
