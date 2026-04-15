export const environment = {
  production: true,
  // API Gateway URL - Point d'entrée principal pour tous les microservices
  apiUrl: 'https://api.jungle-in-english.com',
  
  // Configuration pour les différents environnements
  eurekaUrl: 'https://eureka.jungle-in-english.com/eureka',
  
  // Configuration des microservices (pour référence)
  microservices: {
    jungledraft: 'https://jungledraft.jungle-in-english.com',
    apiGateway: 'https://api.jungle-in-english.com',
    eureka: 'https://eureka.jungle-in-english.com'
  },
  
  // Configuration de logging
  logging: {
    enabled: false,
    level: 'error'
  }
};
