<template>
    <a tabindex="1" class="dropdown-button">
        <i class="material-icons">notifications</i>
        <span id="notificationCount"
              data-badge-caption="" v-if="unread.length > 0">
            {{ unread.length }}
        </span>
        <div class="dropdown-content">
            <h4 class="dropdown-title">Notifications <span class="option" @click="readAll()">Tout marquer comme lu</span></h4>
            <ul>
                <li v-for="notif in sortedNotifications"
                    v-bind:class="{'unread': notif.unread}">
                    <router-link
                        v-bind:to="'/debates/' + notif.debate + ((notif.comment) ? '?comment=' + notif.comment : '') ">
                        <span @click="toggle(notif, false)">
                            <i18n v-bind:path="'notification.'+notif.content.message" tag="div">
                                <template v-for="(value, name) in notif.content.details" v-slot:[name] >
                                    <span class="arg">{{ value }}</span>
                                </template>
                            </i18n>
                            <label>{{ $d(notif.created, 'long') }}</label>
                        </span>
                    </router-link>
                    <tooltiped
                        @click.native="toggle(notif)"
                        v-bind:value="notif.unread ? $t('helper.mark_as_read') : $t('helper.mark_as_unread')">
                        <span class="read-toggle"></span>
                    </tooltiped>
                </li>
            </ul>
        </div>
    </a>
</template>

<script>
    module.exports = {
        computed: {
            notifications() {
                return this.$root.user.notifications;
            },
            unread( ) {
                return this.notifications.filter(n => n.unread);
            },
            sortedNotifications( ) {
                return this.notifications.sort((a, b) => a.created > b.created ? -1 : 1);
            }
        },
        created() {
            this.fetchData();
        },
        methods: {
            fetchData() {
                ArenService.Notifications.getAll({
                    query: {overview: true}
                });
            },
            toggle(notification, value) {
                value = typeof value !== 'undefined' ? value : !notification.unread;
                if (notification.unread !== value) {
                    notification.unread = value;
                    ArenService.Notifications.edit({
                        data: notification
                    });
                }
            },
            readAll() {
                if (this.unread.length > 0) {
                    this.notifications.forEach(n => n.unread = false);
                    ArenService.Notifications.readAll( );
                }
            }
        }
    };
</script>