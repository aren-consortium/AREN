<template>
    <base-layout id="debates">
        <template v-slot:title>
            <h1>{{ $root.isArchive ? $t("menu.archives") : $root.user.is('USER') ? $t('menu.my_debates') : $t('menu.public_debates') }}</h1>
        </template>

        <template v-slot:right>
            <router-link v-if="!$root.isArchive && $root.user.authority !== 'USER'"
                         class="waves-effect waves-light btn"
                         to="/createDebate">
                Nouveau débat
            </router-link>
        </template>

        <debates-grid
            v-bind:categories="categories"
            v-bind:extendable="true">

            <template v-if="!$root.isArchive && $root.user.is('MODO')" v-slot:side.actions="{ debate }">
                <action-button
                    v-bind:tooltip="$t('helper.remove_debate')"
                    v-bind:tooltip-disabled="$t('helper.cannot_remove_debate')"
                    v-bind:disabled="debate.commentsCount !== 0"
                    @press="deleteDebate(document)"
                    icon="delete">
                </action-button>
            </template>
            <template v-if="!$root.isArchive && $root.user.is('MODO')" v-slot:add.contributor="{ debate }">
                <span v-if="$root.user.is('MODO')" class="waves-effect waves-light btn"
                      @click="edit( debate )">
                    Inviter au débat
                </span>
            </template>

        </debates-grid>

        <template v-slot:addons v-if="!$root.isArchive && $root.user.is('MODO')">
            <contributor-modal
                ref="contributorModal">
            </contributor-modal>
        </template>
    </base-layout>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                categories: [],
            };
        },
        created( ) {
            this.fetchData( );
        },
        methods: {
            fetchData( ) {
                if (!this.$root.isArchive) {
                    this.categories = [];
                    let categoryId = this.$route.query.category;
                    ArenService.Debates.getAll({
                        query: categoryId ? {category: categoryId, overview: true} : {overview: true},
                        onSuccess: debates => debates.forEach(d => {
                                if (!this.categories.includes(d.document.category)) {
                                    this.categories.push(d.document.category)
                                }
                            })
                    });
                } else {
                    Archive.importation(() => {
                        this.categories = Archive.Category;
                    });
                }
            },
            edit(debate) {
                this.$refs.contributorModal.debate = debate;
                this.$refs.contributorModal.open( );
            },
            deleteDebate(debate) {
                this.$confirm({
                    title: this.$t("helper.delete_debate", {debateName: debate.document.name}),
                    message: this.$t('helper.do_continue'),
                    callback: (returnValue) => {
                        if (returnValue) {
                            ArenService.Debates.remove({
                                data: debate
                            });
                        }
                    }
                });
            }
        },
        components: {
            'contributor-modal': vueLoader('components/modals/contributorModal'),
            'debates-grid': vueLoader('components/grids/debatesGrid')
        }
    };
</script>