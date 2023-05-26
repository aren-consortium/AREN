<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

  <!DOCTYPE html>
  <html lang="fr">

  <head>
    <base href="${serverRoot}/">

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Plateforme AREN</title>
    <link rel="shortcut icon" href="assets/img/favicon.ico">


    <script>
      BrowserNotification = Notification;
    </script>

    <% if (request.getAttribute("development") !=null) {%>
      <link rel="stylesheet" type="text/css" href="assets/css/quill.snow.css">
      <link rel="stylesheet" type="text/css" href="assets/css/app.css">
      <style>
        html::after {
          content: "DEVELOPMENT";
          position: fixed;
          top: 50%;
          left: 50%;
          font-size: 15vw;
          opacity: 0.1;
          text-align: center;
          pointer-events: none;
          transform: translate(-50%, -50%) rotate(-15deg);
        }
      </style>

      <script src="assets/js/vendors/vue.js"></script>
      <script src="assets/js/vendors/httpVueLoader.js"></script>
      <script src="assets/js/vendors/vue-router.js"></script>
      <script src="assets/js/vendors/quill.js"></script>
      <script src="assets/js/vendors/vue-i18n.js"></script>
      <script src="assets/js/vendors/d3.js"></script>

      <script src="assets/js/service/i18n.js"></script>
      <script src="assets/js/service/aren.js"></script>
      <script src="assets/js/utils.js"></script>
      <script src="assets/js/router.js"></script>
      <script src="assets/js/vueCfg.js"></script>
      <% } else { %>
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
        <script src="assets/js/router.min.js"></script>
        <script src="assets/js/vueCfg.min.js"></script>
        <% }%>
  </head>

  <body>
    <noscript><strong>We're sorry but Aren doesn't work properly without JavaScript enabled. Please enable it to
        continue.</strong></noscript>
    <div v-if="user" id="app" v-bind:class="{help: help}" @click.once="requestNotificationPermission">
      <nav role="navigation">
        <div class="nav-wrapper">
          <router-link to="/" id="logoContainer">
            <img src="assets/img/Aren-logold.svg">
          </router-link>
          <ul class="menu container">
            <li v-if="user.is('MODO')">
              <router-link to="/createDebate" v-bind:class="{ active: $route.path === '/createDebate' }">{{
                $t('menu.open') }}</router-link>
            </li>
            <li v-if="user.is('MODO')">
              <router-link to="/teams"
                v-bind:class="{ active: $route.path === '/teams' || $route.path === '/users' }">{{ $t('menu.teams') }}
              </router-link>
            </li>
            <li v-if="user.is('MODO')">
              <router-link to="/documents" v-bind:class="{ active: $route.path === '/documents'    }">{{
                $t('menu.documents') }}</router-link>
            </li>
            <li>
              <router-link to="/debates" v-bind:class="{ active: $route.path === '/debates' }">{{ user.is('USER') ?
                $t('menu.my_debates') : $t('menu.public_debates') }}</router-link>
            </li>
          </ul>

          <ul class="right">
            <template v-if="user.is('USER')">
              <documented v-bind:value="$t('documentation.notification_panel')">
                <li id="notifications">
                  <notifications-panel v-bind:notifications="user.notifications">
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
                      <!--a class="waves-effect waves-light btn-small" href="" target="_blank" rel="noopener noreferrer">
                        {{ $t('tutorials') }}</a-->
                      <a v-if="user.is('SUPERADMIN')" class="waves-effect waves-light btn-small"
                        @click="$refs.configurationModal.open()"> {{
                        $t('application_settings') }}</a>
                      <a class="waves-effect waves-light btn-small" @click="$refs.passwdModal.open()"> {{
                        $t('change_password') }}</a>
                      <a class="waves-effect waves-light btn-small" @click="logout()">{{ $t('menu.logout') }}</a>
          </ul>
        </div>
        </span>
        </li>
        </documented>
        </template>
        <li v-else>
          <a class="waves-effect waves-light btn" @click="login()">{{ $t('menu.login') }}</a>
          <% if (request.getAttribute("canSignin") !=null) { %>
            <a class="waves-effect waves-light btn" @click="signUp()">{{ $t('menu.signin') }}</a>
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
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="http://www.lirmm.fr"><img src="assets/img/logo-lirmm.png"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="https://lirdef.edu.umontpellier.fr"><img src="assets/img/logo-lirdef.png"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="https://www.ac-montpellier.fr"><img src="assets/img/logo_acad_montpellier.png"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="https://www.facebook.com/mezoa.software"><img src="assets/img/logo-mezoa.png"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="http://forum-debats.fr/"><img src="assets/img/logo-forum-debate.png"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="http://intactile.com"><img src="assets/img/logo-intactile.svg"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="https://www.caissedesdepots.fr/"><img src="assets/img/logo-cdc.jpg"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="https://cartodebat.com"><img src="assets/img/logo-cartodebat.png"></a>
              <a class="grey-text text-lighten-2" target="_blank" rel="noopener noreferrer"
                href="https://controversciences.org"><img src="assets/img/logo-controversciences.png"></a>
            </div>
          </div>
          <div class="col l4 offset-l2 s12">
            <h5 class="white-text">{{ $t('footer.useful_links') }}</h5>
            <ul>
              <li><a class="grey-text text-lighten-2" href="https://github.com/aren-consortium">{{
                  $t('footer.contribute') }}</a></li>
              <li><a class="grey-text text-lighten-2" href="http://www.lirmm.fr/aren/">{{ $t('footer.about') }}</a></li>
              <li><a class="grey-text text-lighten-2" href="mailto:aren@lirmm.fr?subject=[AREN FRONTEND]%20">{{
                  $t('footer.contact') }}</a></li>
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

    <configuration-modal v-if="user.is('SUPERADMIN')" id="configurationModal" ref="configurationModal">
    </configuration-modal>

    <toaster></toaster>
    <tooltip></tooltip>
    <documentation @display="help=$event">
    </documentation>
    <confirm-dialog></confirm-dialog>
    </div>

    <script>
      const ArenService = new ApiService("${serverRoot}/ws", "FR-fr");
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
        created() {
          this.$router.beforeEach((to, from, next) => {
            document.documentElement.scrollTop = 0;
            next();
          });
          ArenService.Users.getLoged({
            onSuccess: (logedUser) => {
              this.user = logedUser;

              if (this.user.is('USER')) {
                this.listener = ArenService.ListenNotifications({
                  onNotification: (notif) => {
                    this.user.notifications.push(notif);
                    let message = this.$t('notification.' + notif.content.message, notif.content.details);
                    this.$toast(message);
                    const notification = new BrowserNotification("AREN", { body: message });
                    notification.onclick = () => {
                      this.$router.push({ path: `/debates/${notif.debate}`, query: { comment: notif.comment } })
                    }
                  }
                });
              }
            },
            onError: (e) => {
              this.logout();
            }
          });

          ArenService.onLoad = (loading) => this.loading = loading;
          ArenService.onError = (json, xhttp) => {
            if (typeof json === "string") {
              this.error = { title: 'internal', message: 'internal_message' };
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
        beforeDestroy() {
          this?.listener?.stop()
        },
        mounted() {
          // Hack to be shure everything is ready
          let interval = setInterval(() => {
            if (this.$confirm) {
              clearInterval(interval);

              if (this.$route.path == '/activation') {
                if (this.$route.query.token) {
                  ArenService.Users.activate({
                    query: { token: this.$route.query.token },
                    onSuccess: () => {
                      this.$confirm({
                        title: this.$t('user_activated'),
                        message: this.$t('helper.user_activated'),
                        isInfo: true
                      });
                      this.$router.push('/')
                    }
                  });
                } else {
                  this.$route.push('/')
                }
              }
              ArenService.Configurations.getAll();
              this.mounted = true;
            }
          }, 100);
        },
        methods: {
          logout() {
            ArenService.Users.logout();
          },
          login() {
            this.$refs.loginModal.open();
          },
          signUp() {
            this.$refs.createUserModal.open();
          },
          forgotPassword() {
            this.$refs.resetPasswdModal.open();
          },
          helpMe() {
            if (!this.help) {
              this.help = true;
              this.$documentation.display(this.$t('documentation.help'));
            }
          },
          requestNotificationPermission() {
            if (BrowserNotification) {
              BrowserNotification?.requestPermission()
            }
          }
        },
        components: {
          "configuration-panel": vueLoader('components/widgets/configurationPanel'),
          "notifications-panel": vueLoader('components/widgets/notificationsPanel'),
          "login-modal": vueLoader('components/modals/loginModal'),
          "create-user-modal": vueLoader('components/modals/createUserModal'),
          "password-modal": vueLoader('components/modals/passwordModal'),
          "reset-password-modal": vueLoader('components/modals/resetPasswordModal'),
          "configuration-modal": vueLoader('components/modals/configurationModal'),
          "toaster": vueLoader('components/singletons/toaster'),
          "confirm-dialog": vueLoader('components/singletons/confirm'),
          "tooltip": vueLoader('components/singletons/tooltip'),
          "documentation": vueLoader('components/singletons/documentation')
        }
      });
    </script>
  </body>

  </html>