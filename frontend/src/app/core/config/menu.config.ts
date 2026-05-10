export interface MenuItem {
  label: string;
  icon: string;
  route: string;
  badge?: number;
  roles: string[];
  children?: MenuItem[];
}

export const MEMBER_MENU: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'home',
    route: '/member/dashboard',
    roles: ['MEMBER'],
  },
  {
    label: 'Activities',
    icon: 'activity',
    route: '/member/activities',
    roles: ['MEMBER'],
  },
  {
    label: 'Goals',
    icon: 'target',
    route: '/member/goals',
    roles: ['MEMBER'],
  },
  {
    label: 'Community Feed',
    icon: 'users',
    route: '/member/community-feed',
    roles: ['MEMBER'],
  },
  {
    label: 'Measurements',
    icon: 'ruler',
    route: '/member/measurements',
    roles: ['MEMBER'],
  },
  {
    label: 'Recommendations',
    icon: 'lightbulb',
    route: '/member/recommendations',
    roles: ['MEMBER'],
  },
  {
    label: 'BMI Calculator',
    icon: 'calculator',
    route: '/member/bmi-calculator',
    roles: ['MEMBER'],
  },
  {
    label: 'My Plans',
    icon: 'clipboard',
    route: '/member/plans',
    roles: ['MEMBER'],
  },
  {
    label: 'Classes',
    icon: 'calendar',
    route: '/member/classes',
    roles: ['MEMBER'],
  },
  {
    label: 'Memberships',
    icon: 'chart',
    route: '/member/memberships',
    roles: ['MEMBER'],
  },
  {
    label: 'Profile',
    icon: 'user',
    route: '/member/profile',
    roles: ['MEMBER'],
  },
];

export const TRAINER_MENU: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'dashboard',
    route: '/trainer/dashboard',
    roles: ['TRAINER'],
  },
  {
    label: 'My Members',
    icon: 'users',
    route: '/trainer/members',
    roles: ['TRAINER'],
  },
  {
    label: 'Workout Plans',
    icon: 'activity',
    route: '/trainer/workout-plans',
    roles: ['TRAINER'],
  },
  {
    label: 'Diet Plans',
    icon: 'clipboard',
    route: '/trainer/diet-plans',
    roles: ['TRAINER'],
  },
  {
    label: 'Classes',
    icon: 'calendar',
    route: '/trainer/classes',
    roles: ['TRAINER'],
  },
  {
    label: 'Profile',
    icon: 'user',
    route: '/trainer/profile',
    roles: ['TRAINER'],
  },
];

export const OWNER_MENU: MenuItem[] = [
  {
    label: 'Owner Dashboard',
    icon: 'dashboard',
    route: '/owner/dashboard',
    roles: ['OWNER'],
  },
  {
    label: 'Gyms',
    icon: 'clipboard',
    route: '/owner/gyms',
    roles: ['OWNER'],
  },
  {
    label: 'Trainers',
    icon: 'users',
    route: '/owner/trainers',
    roles: ['OWNER'],
  },
  {
    label: 'Members',
    icon: 'user',
    route: '/owner/members',
    roles: ['OWNER'],
  },
  {
    label: 'Revenue',
    icon: 'chart',
    route: '/owner/revenue',
    roles: ['OWNER'],
  },
  {
    label: 'Membership Plans',
    icon: 'clipboard',
    route: '/owner/membership-plans',
    roles: ['OWNER'],
  },
  {
    label: 'Subscription',
    icon: 'chart',
    route: '/owner/subscription',
    roles: ['OWNER'],
  },
  {
    label: 'Profile',
    icon: 'user',
    route: '/owner/profile',
    roles: ['OWNER'],
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
    label: 'Trainers',
    icon: 'user',
    route: '/admin/trainers',
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
    label: 'Profile',
    icon: 'user',
    route: '/admin/profile',
    roles: ['ADMIN'],
  },
];
