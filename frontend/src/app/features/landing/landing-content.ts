export const LANDING_CONTENT = {
  brand: {
    name: 'Fitness Management System',
    logoText: 'Fitness Management System',
    tagline: 'Member-First Fitness Experience',
  },
  contact: {
    email: 'hello@fitnessmanagementsystem.app',
    phone: '+91 98765 43210',
    location: 'Bengaluru, India',
  },
  legal: {
    companyName: 'Fitness Management System',
    copyrightYear: '2026',
    privacyUrl: '#',
    termsUrl: '#',
  },
  footer: {
    updatesLabel: 'Get Updates',
    updatesHelp: 'Product releases, best practices, and growth insights.',
  },
  pricing: {
    onboardingNote: 'All plans include app access, trainer support, and progress tracking.',
    plans: [
      {
        name: 'Essential',
        price: '₹999/mo',
        note: 'For members building consistency',
        features: ['Gym Access', 'Basic Class Booking', 'Monthly Progress Summary'],
      },
      {
        name: 'Performance',
        price: '₹1,999/mo',
        note: 'For members focused on measurable goals',
        features: ['Everything in Essential', 'Trainer Program Access', 'Weekly Progress Insights'],
      },
      {
        name: 'Elite',
        price: '₹3,499/mo',
        note: 'For members who want full coaching support',
        features: ['Everything in Performance', 'Priority Class Slots', 'Personalized Nutrition + Plan Reviews'],
      },
    ],
  },
  testimonials: [
    {
      author: 'Ritika Shah',
      role: 'Member, PulseFit',
      message: 'I can see my workouts, memberships, and class bookings in one clean dashboard.',
      image:
        'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=256&q=80',
    },
    {
      author: 'Arjun Mehta',
      role: 'Member, Urban Burn',
      message: 'Booking classes and tracking progress is easy, even on busy days.',
      image:
        'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=256&q=80',
    },
    {
      author: 'Neha Verma',
      role: 'Member, ZenCore Studio',
      message: 'Class booking and progress tracking are simple and actually useful.',
      image:
        'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=256&q=80',
    },
    {
      author: 'Kabir Dev',
      role: 'Member, IronWorks Gym',
      message: 'The trainer integrations and customized goals completely transformed my fitness routine.',
      image:
        'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=256&q=80',
    },
    {
      author: 'Priya Nair',
      role: 'Member, FitSphere',
      message: 'Cleanest interface I have ever used. Upgrading memberships and tracking diet is incredibly seamless.',
      image:
        'https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=256&q=80',
    },
    {
      author: 'Vikram Rao',
      role: 'Member, Peak Gym',
      message: 'Finally, a platform that doesn\'t overcomplicate things. Simple class bookings and precise statistics.',
      image:
        'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=256&q=80',
    }
  ],
} as const;
