import { writeFileSync } from 'node:fs';
import { resolve } from 'node:path';

const apiUrl = process.env.FRONTEND_API_URL || 'https://api.fitness-management.com/api/v1';
const stripeKey = process.env.FRONTEND_STRIPE_PUBLISHABLE_KEY || '';
const debugMode = process.env.FRONTEND_ENABLE_DEBUG_MODE === 'true';

const output = `export const environment = {
  production: true,
  apiUrl: '${apiUrl}',
  apiTimeout: 30000,
  enableDebugMode: ${debugMode},
  stripePublishableKey: '${stripeKey}',
};
`;

const filePath = resolve('src', 'environments', 'environment.prod.ts');
writeFileSync(filePath, output, 'utf8');
console.log(`Prepared production environment at ${filePath}`);
