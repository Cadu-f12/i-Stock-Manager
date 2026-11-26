FROM node:24

WORKDIR /app

COPY package*.json ./

RUN npm ci

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0"]
