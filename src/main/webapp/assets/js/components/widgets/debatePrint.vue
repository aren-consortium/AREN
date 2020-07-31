<template>
    <div v-if="debate" id="debate">

        <div id="header">
            <div>
                <h1>{{ $t('debate')}} - {{ debate.document.name }} </h1>
                <label class="author">{{ $t("by").toLowerCase() }} <span>{{ debate.document.author }}</span> </label>
            </div>
            <div>
                <label class="details">
                    <p>{{ $t('proposed_by') }} : {{ debate.owner.fullName() }}</p>
                    <p>{{ $t('with') }} : {{ debate.teams.map( t => t.name ).join(", ") }}</p>
                </label>
            </div>
        </div>

        <div class="row" id="options">
            <div class="col s6">
            </div>
            <div class="col s6">
                <search-box></search-box>
                <div id="sortCommentsBy">
                    <label>{{ $t('sort_by') }} : </label>
                    <label>
                        <input class="browser-default" name="sortByPosition" type="radio"
                               v-model="sortByPosition" v-bind:value="false" />
                        <i class="clickable">{{ $t('date') }}</i>
                    </label>
                    <label>
                        <input class="browser-default" name="sortByPosition" type="radio"
                               v-model="sortByPosition" v-bind:value="true" />
                        <i class="clickable">{{ $t('position') }}</i>
                    </label>
                </div>
            </div>
        </div>

        <div class="row">
            <div id="documentContainer">
                <div class="scroll-area"
                     ref="documentContainer">
                    <div
                        id="documentDisplay"
                        ref="documentDisplay"
                        v-html="debate.document.content">
                    </div>
                    <bullets-container
                        v-bind:comments="sortedComments">
                    </bullets-container>
                </div>
            </div>

            <div id="commentsContainer">
                <div class="scroll-area"
                     ref="commentsContainer">
                    <comment-widget
                        v-for="comment in sortedComments"
                        v-bind:key="comment.id"
                        v-bind:comment="comment">
                    </comment-widget>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    module.exports = {
        props: ["debate"],
        data( ) {
            return {
                sortByPosition: false,
                mountedChildren: 0,
            };
        },
        computed: {
            sortedComments( ) {
                if (this.sortByPosition) {
                    this.debate.deepSortComments((a, b) => a.compareBoundaryPoints(Range.START_TO_START, b));
                } else {
                    this.debate.deepSortComments((a, b) => (a.created > b.created) ? 1 : -1);
                }

                return this.debate.comments.filter(comment => !comment.parent);
            }
        },
        watch: {
            '$route'() {
                this.fetchData();
            }
        },
        components: {
            'comment-widget': vueLoader('components/widgets/comment'),
            'bullets-container': vueLoader('components/widgets/bulletsContainer')
        }
    };
</script>
