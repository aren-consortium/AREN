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
                        <label v-bind:class="{ chip: true, active: autoSelectedTags.includes(tag)}"
                               v-bind:style="{ background: gradient(tag)}">
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
                        let id = tag.value;
                        if (!tagsObj.hasOwnProperty(id)) {
                            tagsObj[id] = new Tag(tag);
                            tagsObj[id].comments = [];
                            tagsObj[id].colors = {red: 0, blue: 0, green: 0};
                        }
                        if (comment.proposedTags.includes(tag)) {
                            if (tag.negative) {
                                tagsObj[id].colors.red++;
                            } else {
                                tagsObj[id].colors.blue++;
                            }
                        }
                        tagsObj[id].comments.push(comment);
                    });

                    comment.proposedTags.forEach((tag) => {
                        if (!comment.tags.includes(tag)) {
                            let id = tag.value;
                            if (!tagsObj.hasOwnProperty(id)) {
                                tagsObj[id] = new Tag(tag);
                                tagsObj[id].comments = [];
                                tagsObj[id].colors = {red: 0, blue: 0, green: 1};
                            } else {
                                tagsObj[id].colors.green++;
                            }
                            if (!tagsObj[id].comments.includes(comment)) {
                                tagsObj[id].comments.push(comment);
                            }
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
        methods: {
            gradient(tag) {
                let len = tag.comments.length;
                let blue = (tag.colors.blue / len) * 100;
                let red = blue + ((tag.colors.red / len) * 100);
                let green = red + ((tag.colors.green / len) * 100);

                return "linear-gradient(110deg, " + blueColor + " " + blue + "%, transparent " + blue + "%), \
                        linear-gradient(110deg, " + redColor + " " + red + "%, transparent " + red + "%), \
                        linear-gradient(110deg, " + greenColor + " " + green + "%, " + greyColor + " " + green + "%)";
            }
        },
        components: {
            'comment-widget': vueLoader('components/widgets/comment')
        }
    };
</script>