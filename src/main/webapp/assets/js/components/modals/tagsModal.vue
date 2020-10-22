<template>
    <modal-layout>

        <template v-slot:title>
            {{ $t('tags_edition') }}
        </template>

        <div class="selection">
            <div v-html="comment.reformulation" ref="reformulation"></div>
        </div>

        <div v-for="(tag, index) in sortedTags"
             v-bind:class="{ chip: true, negative: (comment.proposedTags.includes(tag) && tag.negative), positive: (comment.proposedTags.includes(tag) && !tag.negative) }"
             @click="toggleTag(tag)"
             @contextmenu.prevent.stop="tag.negative = !tag.negative">
            {{ tag.value }}
        </div>
        <div v-for="(tag, index) in filteredProposedTags"
             class="chip valid">
            {{ tag.value }}
            <i class="close material-icons" @click.stop="comment.proposedTags.remove(tag)">close</i>
        </div>

        <text-input
            v-bind:helper="$t('helper.tag_input_explanations')"
            v-bind:label="$t('proposed_tags')"
            v-model="tagInput"
            @keyup.enter="addTag()">
        </text-input>

        <template v-slot:footer>
            <template v-if="readOnly">
                <button @click="close( );" class="waves-effect waves-green btn-flat">{{ $t('close') }}</button>
            </template>
            <template v-else>
                <button v-if="$root.user.is('ADMIN')" @click="update( );" class="waves-effect waves-green btn left">{{ $t('update_tags') }}</button>
                <button @click="close( );" class="waves-effect waves-green btn-flat">{{ $t('cancel') }}</button>
                <button @click="close( comment );" class="waves-effect waves-green btn-flat">{{ $t('validate') }}</button>
            </template>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        props: ['readOnly'],
        data() {
            return {
                tagInput: "",
                comment: false
            }
        },
        computed: {
            sortedTags() {
                return this.comment.tags ? this.comment.tags.sort((a, b) => b.power - a.power) : [];
            },
            filteredProposedTags() {
                return this.comment.proposedTags ? this.comment.proposedTags.filter((t) => !this.comment.tags.includes(t)) : [];
            }
        },
        methods: {
            toggleTag(tag) {
                if (!this.comment.proposedTags.includes(tag)) {
                    tag.negative = false;
                    this.comment.proposedTags.push(tag);
                } else {
                    this.comment.proposedTags.remove(tag);
                }
            },
            addTag() {
                let tag = new Tag(this.tagInput);
                if (tag.value.length > 0) {
                    let lc = tag.value.toLowerCase();
                    let index = this.comment.proposedTags.findIndex((t) => t.value.toLowerCase() === lc);
                    if (index !== -1) {
                        this.comment.proposedTags[index] = tag;
                    } else {
                        this.comment.proposedTags.push(tag);
                    }
                    this.tagInput = "";
                }
            },
            update() {
                this.$confirm({
                    title: this.$t('update_tags'),
                    message: this.$t('helper.update_tags'),
                    notCloseable: true
                });
                ArenService.Comments.updateTags({
                    id: this.comment.id,
                    onSuccess: (response) => {
                        this.$closeConfirm();
                    },
                    onError: () => {
                        this.$confirm({
                            title: this.$t('error.Insertion error'),
                            message: this.$t('error.tag_update'),
                            isInfo: true
                        });
                    },
                });
            },
            afterClose() {
                this.comment = false;
            }
        }
    };
</script>
