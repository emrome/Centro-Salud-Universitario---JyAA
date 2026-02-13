  export const AppRoutes = {
  Public: {
    Root: '/',
    Login: '/login',
    Register: '/register',
  },

  Admin: {
    Root: '/admin',
    Dashboard: '/admin',

    Campaigns: {
      List: '/admin/campaigns',
      New: '/admin/campaigns/new',
      Edit: (id: number) => `/admin/campaigns/${id}/edit`,

      Events: {
        List: (campaignId: number) => `/admin/campaigns/${campaignId}/events`,
        New: (campaignId: number) => `/admin/campaigns/${campaignId}/events/new`,
        Edit: (campaignId: number, eventId: number) =>
          `/admin/campaigns/${campaignId}/events/${eventId}/edit`
      }
    },

    Representatives: {
      List: '/admin/users/representatives',
      New: '/admin/users/representatives/new',
      Edit: (id: number) => `/admin/users/representatives/${id}/edit`,
    },

    Surveyors: {
      List: '/admin/surveyors',
      New: '/admin/surveyors/new',
      Edit: (id: number) => `/admin/surveyors/${id}`,
    },

    Admins: {
      List: '/admin/users/admins',
      New: '/admin/users/admins/new',
      Edit: (id: number) => `/admin/users/admins/${id}/edit`,
    },

    HealthStaff: {
      List: '/admin/users/health-staff',
      New: '/admin/users/health-staff/new',
      Edit: (id: number) => `/admin/users/health-staff/${id}/edit`,
    },

    Organizations: {
      List: '/admin/organizations',
      New: '/admin/organizations/new',
      Edit: (id: number) => `/admin/organizations/${id}/edit`,
    },

    UserEnablement: {
      List: '/admin/users/enablement',
    },

    Neighborhoods: {
      List: '/admin/neighborhoods',
      New: '/admin/neighborhoods/new',
      Edit: (id: number) => `/admin/neighborhoods/${id}/edit`,

      Zones: {
        List: (neighId: number) => `/admin/neighborhoods/${neighId}/zones`,
        New: (neighId: number) => `/admin/neighborhoods/${neighId}/zones/new`,
        Edit: (neighId: number, zoneId: number) =>
          `/admin/neighborhoods/${neighId}/zones/${zoneId}/edit`
      },

    }
  },

  Representative: {
    Root: '/representative',
    Dashboard: '/representative',

    ReportRequests: {
      List: '/representative/report-requests'
    }
  },

  HealthStaff: {
    Root: '/health-staff',
    Dashboard: '/health-staff',

    ReportRequests: {
      List: '/health-staff/report-requests'
    }
  },

  Analytics: {
    Summary: '/analytics/summary',
    Map: '/analytics/map',
  }
};
