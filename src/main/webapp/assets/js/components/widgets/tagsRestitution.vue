<template>
    <div>
        <div>
            <label>
                <span class="clickable" @click="selectedTags = []">{{ $t('unselect_all') }}</span>
            </label>
        </div>
        <div id="tagsRestitution" class="row">
            <div class="col s6 center-align">
                <template v-for="tag in tags">
                    <tooltiped v-bind:value="$tc('nb_comments', tag.comments.length)">
                        <label v-bind:class="{ chip: true, negative: (tag.color === -1), positive: (tag.color === 1), valid: (tag.color === 2), active: autoSelectedTags.includes(tag) }">
                            <input type="checkbox" v-bind:value="tag" v-model="selectedTags"/>
                            <span>{{ tag.value }}</span>
                        </label>
                    </tooltiped>
                </template>
            </div>
            <div class="col s6">
                <comment-widget v-for="comment in commentsIntersection"
                                v-bind:key="'comment_'+comment.id"
                                v-bind:preview="true"
                                v-bind:comment="comment">
                </comment-widget>
            </div>
        </div>
    </div>
</template>

<script>
    module.exports = {
        props: {
            debate: Object
        },
        data() {
            return {
                geryTags: [],
                blueTags: [],
                redTags: [],
                greenTags: [],

                selectedTags: [],
                autoSelectedTags: [],
                tags: [],
                commentsIntersection: []
            }
        },
        created() {
            let tagsObj = {};

            this.debate.comments.forEach((comment) => {
                if (comment.tags) {
                    comment.tags.forEach((tag) => {
                        let id = (tag.negative ? "-" : "") + tag.value;
                        if (!tagsObj.hasOwnProperty(id)) {
                            tagsObj[id] = new Tag(tag);
                            tagsObj[id].comments = [];
                            tagsObj[id].color = 0;
                        }
                        tagsObj[id].comments.push(comment);
                    });
                    comment.proposedTags.forEach((tag) => {
                        let id = (tag.negative ? "-" : "") + tag.value;
                        if (!tagsObj.hasOwnProperty(id)) {
                            tagsObj[id] = new Tag(tag);
                            tagsObj[id].comments = [];
                            tagsObj[id].color = 2;
                        } else if (tagsObj[id].color !== 2) {
                            tagsObj[id].color = tag.negative ? -1 : 1;
                        }
                        if (tagsObj[id].color === 2) {
                            tagsObj[id].comments.push(comment);
                        }
                    });
                }
            });
            this.tags = Object.values(tagsObj).sort((a, b) => b.comments.length - a.comments.length);
        },
        watch: {
            selectedTags(val) {
                this.commentsIntersection = [];
                this.autoSelectedTags = val;
                if (val.length !== 0) {
                    this.commentsIntersection = val[0].comments;
                    for (let i = 1; i < val.length; i++) {
                        this.commentsIntersection = this.commentsIntersection.filter
                                (value => val[i].comments.includes(value));
                    }

                    if (this.commentsIntersection.length > 0) {
                        this.autoSelectedTags = this.tags.filter(tag =>
                            this.commentsIntersection.every(comment => tag.comments.includes(comment))
                        );
                    }
                }
            }
        },
        components: {
            'comment-widget': vueLoader('components/widgets/comment')
        }
    };
</script>