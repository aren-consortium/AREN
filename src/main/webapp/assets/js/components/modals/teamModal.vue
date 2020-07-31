<template>
    <modal-layout>

        <template v-slot:title> {{ team.id ? $t('team_edition') : $t('team_creation') }} </template>

        <div>
            <text-input type="text"
                        v-model="team.name"
                        v-bind:label="$t('title')"
                        v-bind:placeholder="$t('helper.title_placeholder')">
            </text-input>
        </div>

        <template v-slot:footer>
            <button @click="close( )" class="waves-effect waves-green btn-flat">Annuler</button>
            <button @click="close( team );" class="waves-effect waves-green btn-flat">Valider</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        data( ) {
            return {
                team: new Team( )
            };
        }
    };
</script>