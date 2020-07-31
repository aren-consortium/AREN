<template>
    <select class="browser-default" v-model="selectedId" @change="fetchInstitution()" >
        <option v-for="institution in institutions" v-bind:value="institution.id">
            {{ displayName(institution) }}
        </option>
    </select>
</template>

<script>
    module.exports = {
        data: function ( ) {
            return {
                institutions: ArenService.Store.Institution,
                selectedId: this.$root.user.institution ? this.$root.user.institution.id : 0
            };
        },
        created: function ( ) {
            this.fetchData( );
        },
        methods: {
            fetchData( ) {
                ArenService.Institutions.getAll({
                    onSuccess: institutions => {
                        institutions.sort((a, b) => a.id === 0 ? -1 : b.id === 0 ? 1 : (this.displayName(a) > this.displayName(b)) ? 1 : -1);
                        this.fetchInstitution( );
                    }
                });
            },
            fetchInstitution( ) {
                ArenService.Institutions.get({
                    id: this.selectedId,
                    onSuccess: institution => this.$emit('institution-change', institution)
                });
            },
            displayName(inst) {
                if (inst.id === 0) {
                    return "Sans établissement";
                } else {
                    return inst.type + inst.name;
                }
            }
        }
    };
</script>