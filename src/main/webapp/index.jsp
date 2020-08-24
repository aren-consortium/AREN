<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.*" %>
<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("application");
    String productionMode = resource.getString("production");
    String serverRoot = resource.getString("reverse-proxy");
    if (serverRoot.length() == 0) {
        serverRoot = request.getRequestURL().substring(0, request.getRequestURL().length() - "/index.jsp".length());
    }
    boolean canMail = resource.getString("smtp.server").length() > 0;%>

<!DOCTYPE html>
<html lang="fr">

    <head>
        <base href="<%=serverRoot%>/">

        <meta charset="utf-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width,initial-scale=1">
        <title>Plateforme AREN</title>
        <link rel="shortcut icon" href="assets/img/favicon.ico">


        <script>
            BrowserNotification = Notification;
        </script>

        <% if (productionMode.equals("true")) {%>
        <link rel="stylesheet" type="text/css" href="assets/css/quill.snow.min.css">
        <link rel="stylesheet" type="text/css" href="assets/css/app.min.css">

        <script src="assets/js/vendors/vue.js"></script>
        <script src="assets/js/vendors/httpVueLoader.min.js"></script>
        <script src="assets/js/vendors/vue-router.min.js"></script>
        <script src="assets/js/vendors/quill.min.js"></script>
        <script src="assets/js/vendors/vue-i18n.min.js"></script>
        <script src="assets/js/vendors/d3.min.js"></script>

        <script src="assets/js/service/i18n.min.js"></script>
        <script src="assets/js/service/aren.min.js"></script>
        <script src="assets/js/utils.min.js"></script>
        <script src="assets/js/archives/import.min.js"></script>
        <script src="assets/js/router.min.js"></script>
        <script src="assets/js/vueCfg.min.js"></script>
        <% } else { %>
        <link rel="stylesheet" type="text/css" href="assets/css/quill.snow.css">
        <link rel="stylesheet" type="text/css" href="assets/css/app.css">

        <script src="assets/js/vendors/vue.js"></script>
        <script src="assets/js/vendors/httpVueLoader.js"></script>
        <script src="assets/js/vendors/vue-router.js"></script>
        <script src="assets/js/vendors/quill.js"></script>
        <script src="assets/js/vendors/vue-i18n.js"></script>
        <script src="assets/js/vendors/d3.js"></script>

        <script src="assets/js/service/i18n.js"></script>
        <script src="assets/js/service/aren.js"></script>
        <script src="assets/js/utils.js"></script>
        <script src="assets/js/archives/import.js"></script>
        <script src="assets/js/router.js"></script>
        <script src="assets/js/vueCfg.js"></script>
        <% }%>
    </head>

    <body>
        <noscript><strong>We're sorry but Aren doesn't work properly without JavaScript enabled. Please enable it to continue.</strong></noscript>
        <div v-if="user" id="app" v-bind:class="{help: help}">
            <nav role="navigation">
                <div class="nav-wrapper" >
                    <router-link to="/" id="logoContainer">
                        <img src="assets/img/Aren-logold.svg">
                    </router-link>
                    <ul class="menu container">
                        <li v-if="user.is('MODO')"><router-link to="/createDebate" v-bind:class="{ active: $route.path === '/createDebate' }" >{{ $t('menu.open') }}</router-link></li>
                        <li v-if="user.is('MODO')"><router-link to="/teams"        v-bind:class="{ active: $route.path === '/teams'        }" >{{ $t('menu.teams') }}</router-link></li>
                        <li v-if="user.is('MODO')"><router-link to="/documents"    v-bind:class="{ active: $route.path === '/documents'    }" >{{ $t('menu.documents') }}</router-link></li>
                        <li><router-link to="/debates"   v-bind:class="{ active: $route.path === '/debates' }"  >{{ user.is('USER') ? $t('menu.my_debates') : $t('menu.public_debates') }}</router-link></li>
                        <li><router-link to="/archives"  v-bind:class="{ active: $route.path === '/archives' }" >{{ $t('menu.archives') }}</router-link></li>
                    </ul>

                    <ul class="right">
                        <template v-if="user.is('USER')">
                            <documented v-bind:value="$t('documentation.notification_panel')">
                                <li id="notifications">
                                <notifications-panel
                                    v-bind:notifications="user.notifications">
                                </notifications-panel>
                                </li>
                            </documented>
                            <documented v-bind:value="$t('documentation.user_panel')">
                                <li id="userSettings">
                                    <span tabindex="1" class="dropdown-button">
                                        <span class="btn-floating btn waves-effect waves-light">
                                            <template v-if="user.firstName">
                                                {{ user.firstName[0] }}.{{ $root.user.lastName[0] }}
                                            </template>
                                            <template v-else>
                                                {{ user.username[0] }}.{{ $root.user.username[1] }}
                                            </template>
                                        </span>
                                        <div class="dropdown-content">
                                            <h4 class="dropdown-title"> {{ $root.user.fullName() }}</h4>
                                            <a class="waves-effect waves-light btn-small" @click="$refs.passwdModal.open()"> {{ $t('change_password') }}</span>
                                                <a class="waves-effect waves-light btn-small" @click="logout()">{{ $t('menu.logout') }}</a>
                                                </ul>
                                        </div>
                                    </span>
                                </li>
                            </documented>
                        </template>
                        <li v-else>
                            <a class="waves-effect waves-light btn" @click="login()">{{ $t('menu.login') }}</a>
                            <% if (canMail) {%>
                            <a class="waves-effect waves-light btn" @click="signIn()">{{ $t('menu.signin') }}</a>
                            <% }%>
                        </li>
                        <li id="help" @click="helpMe()">
                            <i class="material-icons">help</i>
                        </li>
                    </ul>
                </div>
            </nav>

            <div v-if="loading" id="loading" class="progress">
                <div class="indeterminate"></div>
            </div>

            <div class="current-view-wrapper">
                <router-view v-if="mounted" class="current-view"></router-view>
            </div>

            <footer class="page-footer">
                <div class="container">
                    <div class="row">
                        <div class="col l6 s12">
                            <h5 class="white-text">{{ $t("footer.collaborators") }}</h5>
                            <div class="contributors">
                                <a class="grey-text text-lighten-2" href="https://lirdef.edu.umontpellier.fr"><img src="assets/img/logo-lirdef.png"></a>
                                <a class="grey-text text-lighten-2" href="http://www.lirmm.fr"><img src="assets/img/logo-lirmm.png"></a>
                                <a class="grey-text text-lighten-2" href="https://www.ac-montpellier.fr"><img src="assets/img/logo_acad_montpellier.png"></a>
                                <a class="grey-text text-lighten-2" href="https://cartodebat.com"><img src="assets/img/logo-cartodebat.svg"></a>
                                <a class="grey-text text-lighten-2" href="http://intactile.com"><img src="assets/img/logo-intactile.svg"></a>
                                <a class="grey-text text-lighten-2" href="https://www.facebook.com/mezoa.software"><img src="assets/img/logo-mezoa.png"></a>
                                <a class="grey-text text-lighten-2" href="http://forum-debats.fr/"><img src="assets/img/logo-forum-debate.png"></a>
                                <a class="grey-text text-lighten-2" href="https://controversciences.org"><img src="assets/img/logo-controversciences.png"></a>
                            </div>
                        </div>
                        <div class="col l4 offset-l2 s12">
                            <h5 class="white-text">{{ $t('footer.useful_links') }}</h5>
                            <ul>
                                <li><a class="grey-text text-lighten-2" href="https://github.com/aren-consortium">{{ $t('footer.contribute') }}</a></li>
                                <li><a class="grey-text text-lighten-2" href="http://www.lirmm.fr/aren/">{{ $t('footer.about') }}</a></li>
                                <li><a class="grey-text text-lighten-2" href="mailto:aren@lirmm.fr?subject=[AREN FRONTEND]%20">{{ $t('footer.contact') }}</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div class="footer-copyright">
                    <div class="container">
                        © 2020 Copyright AREN Consortium, All rights reserved.
                        <a class="grey-text text-lighten-2 right" href="https://opensource.org/licenses/MIT">Licence MIT</a>
                    </div>
                </div>
            </footer>

            <template v-if="user.is('GUEST')">
                <login-modal ref="loginModal" @forgot-password="forgotPassword"></login-modal>
                <create-user-modal ref="createUserModal"></create-user-modal>
                <reset-password-modal ref="resetPasswdModal"></reset-password-modal>
            </template>

            <password-modal ref="passwdModal"></password-modal>

            <toaster></toaster>
            <tooltip></tooltip>
            <documentation></documentation>
            <confirm-dialog></confirm-dialog>
        </div>

        <script>
            BrowserNotification.requestPermission();
            const ArenService = new ApiService(document.baseURI + "ws", "FR-fr");
            let styles = window.getComputedStyle(document.documentElement);
            const redColor = styles.getPropertyValue('--red-color');
            const blueColor = styles.getPropertyValue('--blue-color');
            const greenColor = styles.getPropertyValue('--green-color');
            const greyColor = styles.getPropertyValue('--grey-color');

            new Vue({
                el: "#app",
                router,
                i18n,
                data: {
                    user: false,
                    loading: false,
                    error: false,
                    mounted: false,
                    help: false
                },
                computed: {
                    isArchive: {
                        get() {
                            return this.$route.path.includes("archives");
                        },
                        set() {}
                    }
                },
                created() {
                    this.$router.beforeEach((to, from, next) => {
                        document.documentElement.scrollTop = 0;
                        next();
                    });
                    ArenService.Users.getLoged({
                        onSuccess: (logedUser) => {
                            this.user = logedUser;

                            ArenService.NotificationListener.listen({
                                onMessage: (notif) => {
                                    this.user.notifications.push(notif);
                                    let message = this.$t('notification.' + notif.content.message, notif.content.details);
                                    this.$toast(message);
                                    new BrowserNotification("AREN", {body: message});
                                }
                            });
                        },
                        onError: (e) => {
                            this.logout();
                        }
                    });

                    ArenService.onLoad = (loading) => this.loading = loading;
                    ArenService.onError = (json, xhttp) => {
                        if (typeof json === "string") {
                            this.error = {title: 'internal', message: 'internal_message'};
                        } else {
                            this.error = json;
                        }
                        this.$confirm({
                            title: this.$t('error.' + this.error.title),
                            message: this.$t('error.' + this.error.message, this.error.details),
                            isInfo: true
                        });
                    };
                },
                mounted() {
                    // Hack to be shure everything is ready
                    let interval = setInterval(() => {
                        if (this.$confirm) {
                            clearInterval(interval);

                            if (this.$route.query.activation && this.$route.query.token) {
                                ArenService.Users.activate({
                                    query: {token: this.$route.query.token},
                                    onSuccess: () => {
                                        this.$confirm({
                                            title: this.$t('user_activated'),
                                            message: this.$t('helper.user_activated'),
                                            isInfo: true
                                        });
                                        this.$router.replace({query: null});
                                    }
                                });
                            }
                            this.mounted = true;
                        }
                    }, 100);
                },
                methods: {
                    logout() {
                        ArenService.Users.logout({
                            onSuccess: () => document.location.replace("")
                        });
                    },
                    login( ) {
                        this.$refs.loginModal.open((returnVal) => {
                            if (returnVal) {
                                document.location.reload(true);
                            }
                        });
                    },
                    signIn( ) {
                        this.$refs.createUserModal.open((newUser) => {
                            if (newUser) {
                                ArenService.Users.create({
                                    data: newUser,
                                    query: {returnUrl: "<%=serverRoot%>?activation=true&token={token}"},
                                    onSuccess: () => this.$confirm({
                                            title: this.$t('user_created'),
                                            message: this.$t('helper.user_created_email'),
                                            isInfo: true
                                        })
                                });
                            }
                        });
                    },
                    forgotPassword() {
                        this.$refs.resetPasswdModal.open((username) => {
                            if (username) {
                                ArenService.Users.resetPasswd({
                                    data: username,
                                    query: {returnUrl: "<%=serverRoot%>?resetPassword=true&token={token}"},
                                    onSuccess: () => this.$confirm({
                                            title: this.$t('password_forgotten'),
                                            message: this.$t('helper.password_forgotten_email'),
                                            isInfo: true
                                        })
                                });
                            }
                        });
                    },
                    helpMe() {
                        this.help = !this.help;
                        if (this.help) {
                            this.$documentation.display(this.$t('documentation.help'));
                        } else {
                            this.$documentation.hide();
                        }
                    }
                },
                components: {
                    "notifications-panel": vueLoader('components/widgets/notificationsPanel'),
                    "login-modal": vueLoader('components/modals/loginModal'),
                    "create-user-modal": vueLoader('components/modals/createUserModal'),
                    "password-modal": vueLoader('components/modals/passwordModal'),
                    "reset-password-modal": vueLoader('components/modals/resetPasswordModal'),
                    "toaster": vueLoader('components/singletons/toaster'),
                    "confirm-dialog": vueLoader('components/singletons/confirm'),
                    "tooltip": vueLoader('components/singletons/tooltip'),
                    "documentation": vueLoader('components/singletons/documentation')
                }
            });
        </script>
    </body>

</html>