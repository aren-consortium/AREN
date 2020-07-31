<template>
    <modal-layout>

        <template v-slot:title>Inviter à débatre</template>

        <contirbutors-widget v-if="debate">
            <template v-slot:team.side.actions="{ team }">
                <toggle-action-button
                    v-bind:checked="debate.teams.includes( team )"
                    @toggle="toggleTeam( team )">
                </toggle-action-button>
            </template>

            <template v-slot:user.side.actions="{ user }">
                <toggle-action-button
                    v-bind:checked="debate.guests.includes( user )"
                    @toggle="toggleGuest( user )">
                </toggle-action-button>
            </template>
        </contirbutors-widget>

        <template v-slot:footer>
            <button @click="close()" class="waves-effect waves-green btn-flat">Fermer</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data: function ( ) {
            return {
                debate: false,
                institution: new Institution( )
            };
        },
        methods: {
            institutionChanged(institution) {
                this.institution = institution;
            },
            toggleTeam(team) {
                if (!this.debate.teams.includes(team)) {
                    ArenService.Debates.addTeam({
                        id: this.debate.id,
                        data: team
                    });
                } else {
                    ArenService.Debates.removeTeam({
                        id: this.debate.id,
                        data: team
                    });
                }
            },
            toggleGuest(user) {
                if (!this.debate.guests.includes(user)) {
                    ArenService.Debates.addGuest({
                        id: this.debate.id,
                        data: user
                    });
                } else {
                    ArenService.Debates.removeGuest({
                        id: this.debate.id,
                        data: user
                    });
                }
            }
        },
        components: {
            'contirbutors-widget': vueLoader('components/widgets/contributors')
        }
    };
</script>