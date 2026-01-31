export interface MenuItem {
  label: string;
  icon: string;
  route: string;
  badge?: number;
  roles: string[];
  children?: MenuItem[];
}

export const USER_MENU: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'home',
    route: '/user/dashboard',
    roles: ['USER', 'ADMIN'],
  },
  {
    label: 'Activities',
    icon: 'activity',
    route: '/user/activities',
    roles: ['USER', 'ADMIN'],
  },
  {
    label: 'Goals',
    icon: 'target',
    route: '/user/goals',
    roles: ['USER', 'ADMIN'],
  },
  {
    label: 'Measurements',
    icon: 'ruler',
    route: '/user/measurements',
    roles: ['USER', 'ADMIN'],
  },
  {
    label: 'Recommendations',
    icon: 'lightbulb',
    route: '/user/recommendations',
    roles: ['USER', 'ADMIN'],
  },
  {
    label: 'BMI Calculator',
    icon: 'calculator',
    route: '/user/bmi-calculator',
    roles: ['USER', 'ADMIN'],
  },
  {
    label: 'Profile',
    icon: 'user',
    route: '/user/profile',
    roles: ['USER', 'ADMIN'],
  },
];

export const ADMIN_MENU: MenuItem[] = [
  {
    label: 'Admin Dashboard',
    icon: 'dashboard',
    route: '/admin/dashboard',
    roles: ['ADMIN'],
  },
  {
    label: 'Users',
    icon: 'users',
    route: '/admin/users',
    roles: ['ADMIN'],
  },
  {
    label: 'Activities',
    icon: 'activity',
    route: '/admin/activities',
    roles: ['ADMIN'],
  },
  {
    label: 'Analytics',
    icon: 'chart',
    route: '/admin/analytics',
    roles: ['ADMIN'],
  },
  {
    label: 'User View',
    icon: 'home',
    route: '/user/dashboard',
    roles: ['ADMIN'],
  },
];
