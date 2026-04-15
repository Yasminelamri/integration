export const environment = {
  production: false,
  // API Gateway URL - Point d'entrée principal pour tous les microservices
  apiUrl: 'http://localhost:8085',
  
  // Configuration pour les différents environnements
  eurekaUrl: 'http://localhost:8761/eureka',
  
  // Configuration des microservices (pour référence)
  microservices: {
    jungledraft: 'http://localhost:9090',
    apiGateway: 'http://localhost:8085',
    eureka: 'http://localhost:8761'
  },
  
  // Configuration de logging
  logging: {
    enabled: true,
    level: 'debug'
  }
};
