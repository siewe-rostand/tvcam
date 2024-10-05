### STAGE 1: Build stage ###
FROM node:22.8 AS build-stage

WORKDIR /app

#### copy both 'package.json' and 'package-lock.json' (if available)
COPY package*.json ./

#### install dependencies
RUN npm cache clean --force

RUN npm install

#### install angular cli
RUN npm install -g @angular/cli@18

#### copy things
COPY . .



#### generate build --prod
RUN ng build --configuration=production

### STAGE 2: Run ###
FROM nginx:alpine

#### copy nginx conf
COPY ./nginx.conf /etc/nginx/nginx.conf

#### copy artifact build from the 'build environments'
COPY --from=build-stage /app/dist/front/ /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]

EXPOSE 80