export const environment = {
  production: false,
  apiUrl: 'http://localhost:8888',
  auth: {
    loginUrl: 'http://localhost:8888/api/auth/signin',
    registerUrl: 'http://localhost:8888/api/auth/signup'
  },
  invoice: {
    baseUrl: 'http://localhost:8888/api/factures'
  },
  product: {
    baseUrl: 'http://localhost:8888/api/produits'
  },
  client: {
    baseUrl: 'http://localhost:8888/api/clients'
  }
};
