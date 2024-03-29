const router = new VueRouter({
    base: "",
    mode: "history",
    routes: [
        {path: '/', component: vueLoader('views/home')},
        {path: '/activation', component: vueLoader('views/home')},
        {path: '/resetPassword', component: vueLoader('views/home')},
        {path: '/createDebate', component: vueLoader('views/createDebate')},
        {path: '/teams', component: vueLoader('views/teams')},
        {path: '/users', component: vueLoader('views/teams')},
        {path: '/teams/:id', component: vueLoader('views/team')},
        {path: '/documents', component: vueLoader('views/documents')},
        {path: '/documents/:id', component: vueLoader('views/document')},
        {path: '/debates', component: vueLoader('views/debates')},
        {path: '/debates/:id', component: vueLoader('views/debate')},
        {path: '/debates/:id/restitution', component: vueLoader('views/restitution')},
        {path: '/debates/:id/print', component: vueLoader('views/debatePrint')},
        {path: '/aaf', component: vueLoader('views/aaf')},
        {path: '/tags', component: vueLoader('views/updateTags')},
        {path: '*', component: vueLoader('views/404')}
    ]
});