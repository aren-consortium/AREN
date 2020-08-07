<template>
    <base-layout id="document">
        <template v-slot:title>
            <h1>{{ $t('document') }} - </h1>
            <h1>
                <text-input class="inline"
                            v-bind:disabled="!editionMode" ref="name" type="textarea"
                            v-model="document.name"
                            v-bind:placeholder="$t('title')"></text-input>
            </h1>
            <label>{{ $t("by").toLowerCase() }}</label>
            <text-input class="author inline"
                        v-bind:disabled="!editionMode" ref="author" type="textarea"
                        v-model="document.author"
                        v-bind:placeholder="$t('author')"
                        maxlength="30"></text-input>
        </template>

        <template v-slot:right>
            <span v-if="editionMode" class="waves-effect waves-light btn"
                  @click="saveDocument()">
                {{ $t("save") }}
            </span>
            <span v-if="editionMode" class="waves-effect waves-light btn"
                  @click="cancelEdition()">
                {{ $t("cancel") }}
            </span>

            <span v-if="!editionMode" class="waves-effect waves-light btn"
                  @click="fetchData(); editionMode = true;">
                {{ $t("modify") }}
            </span>
        </template>

        <div id="documentDisplay" v-bind:class="[ 'container', { 'in-edition': editionMode === true } ]">
            <tooltiped tag="div"
                       v-bind:value="(editionMode && document.debatesCount > 0) ? $t('helper.cannot_edit_document') : '' ">
                <wysiwyg-editor v-model="document.content"
                                v-bind:enabled="editionMode && !document.debatesCount">
                </wysiwyg-editor>
            </tooltiped>
        </div>

    </base-layout>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                document: false,
                unmodifiedDocument: new Document( ),
                editionMode: false
            };
        },
        created() {
            if (!this.$root.user.is('MODO')) {
                this.$router.push("/404");
                return;
            }
        },
        mounted( ) {
            this.fetchData( );
        },
        methods: {
            fetchData( ) {
                if (this.$route.params.id !== "new") {
                    ArenService.Documents.get({
                        id: this.$route.params.id,
                        onSuccess: (document) => {
                            this.document = document;
                            this.copyDocument(this.document, this.unmodifiedDocument);
                        }
                    });
                } else {
                    this.document = new Document( );
                    this.document.category = new Category( );
                    this.document.category.id = this.$route.query.category;
                    this.editionMode = true;
                }
            },
            cancelEdition( ) {
                this.copyDocument(this.unmodifiedDocument, this.document);
                this.editionMode = false;
                if (this.$route.params.id === "new") {
                    this.$router.push('/documents');
                }
            },
            saveDocument( ) {
                this.editionMode = false;
                ArenService.Documents.createOrUpdate({
                    data: this.document,
                    onSuccess: (document) => {
                        if (this.$route.params.id === "new") {
                            this.$router.push('/documents/' + document.id);
                        }
                    }
                });
            },
            copyDocument(from, to) {
                to.name = from.name;
                to.author = from.author;
                to.content = from.content;
            }
        },
        components: {
            'wysiwyg-editor': vueLoader('components/widgets/wysiwygEditor')
        }
    };
</script>
