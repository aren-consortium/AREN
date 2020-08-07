<template>
    <base-layout id="documents">
        <template v-slot:title>
            <h1>Documents</h1>
        </template>

        <template v-slot:right>
            <span class="waves-effect waves-light btn"
                  @click="createOrUpdateCategory()">
                Nouvelle catégorie
            </span>
        </template>

        <documents-grid ref="documentGrid"
                        v-bind:categories="categories"
                        v-bind:extendable="true"
                        v-bind:actions="[ 2 ]">

            <template v-slot:card.actions="{ category }">
                <action-button
                    v-bind:tooltip="$t('helper.add_document')"
                    @press="$router.push({ path: '/documents/new', query: { category: category.id }})"
                    icon="note_add">
                </action-button>
                <action-button
                    v-bind:tooltip="$t('helper.edit_category')"
                    @press="createOrUpdateCategory(category)"
                    icon="edit">
                </action-button>
                <action-button
                    v-bind:tooltip="$t('helper.remove_category')"
                    v-bind:tooltip-disabled="$t('helper.cannot_remove_category')"
                    v-bind:disabled="category.documents.length !== 0"
                    @press="deleteCategory(category)"
                    icon="delete">
                </action-button>
            </template>

            <template v-slot:side.actions="{ document }">
                <action-button
                    v-bind:tooltip="$t('helper.copy_document')"
                    @press="duplicateDocument(document)"
                    icon="content_copy">
                </action-button>
                <action-button
                    v-bind:tooltip="$t('helper.remove_document')"
                    v-bind:tooltip-disabled="$t('helper.cannot_remove_document')"
                    v-bind:disabled="document.debatesCount !== 0"
                    @press="deleteDocument(document)"
                    icon="delete">
                </action-button>
            </template>

        </documents-grid>

        <template v-slot:addons>
            <category-modal
                ref="categoryModal">
            </category-modal>
        </template>
    </base-layout>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                categories: ArenService.Store.Category
            };
        },
        created( ) {
            if (!this.$root.user.is('MODO')) {
                this.$router.push("/404");
                return;
            }
            this.fetchData( );
        },
        methods: {
            fetchData( ) {
                ArenService.Categories.getAll();
            },
            duplicateDocument(document) {
                let documentCopy = new Document({
                    name: document.name + " - copie",
                    author: document.author,
                    content: document.content,
                    category: {id: document.category.id}
                });
                ArenService.Documents.duplicate({
                    id: document.id
                });
            },
            createOrUpdateCategory(category = {}) {
                this.$refs.categoryModal.category = new Category(category);
                this.$refs.categoryModal.open((updatedCat) => {
                    if (updatedCat) {
                        ArenService.Categories.createOrUpdate({
                            data: updatedCat,
                            onSuccess: (returnedCategory) => {
                                this.$nextTick(function ( ) {
                                    this.$refs.documentGrid.selectRow('group' + returnedCategory.id);
                                });
                            }
                        });
                    }
                });
            },
            deleteDocument(document) {
                this.$confirm({
                    title: this.$t("helper.delete_document", {documentName: document.name}),
                    message: this.$t('helper.do_continue'),
                    callback: (returnValue) => {
                        if (returnValue) {
                            ArenService.Documents.remove({
                                data: document
                            });
                        }
                    }
                });
            },
            deleteCategory(category) {
                this.$confirm({
                    title: this.$t("helper.delete_category", {debateName: category.name}),
                    message: this.$t('helper.do_continue'),
                    callback: (returnValue) => {
                        if (returnValue) {
                            ArenService.Categories.remove({
                                data: category
                            });
                        }
                    }
                });
            }
        },
        components: {
            'category-modal': vueLoader('components/modals/categoryModal'),
            'documents-grid': vueLoader('components/grids/documentsGrid')
        }
    };
</script>