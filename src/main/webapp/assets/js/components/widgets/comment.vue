<template>
    <div v-bind:class="{'comment-container': true, moderated: comment.moderated}">
        <div ref="comment"
             v-bind:id="'comment_' + comment.id"
             v-bind:class="'comment ' + comment.opinion.toLowerCase()">
            <span class="focuser" tabindex="1"></span>
            <div v-if="isVisible" ref="innerComment">
                <label class="comment-header">
                    <h2 class="username">{{ comment.owner.authority === 'DELETED' ? $t('anonymous_user') : comment.owner.fullName() }}</h2>
                    <router-link class="clickable" v-bind:to="'/debates/' + comment.debate.id + '?comment=' + comment.id">
                        <time>{{ comment.created.toLocaleString() }}</time>
                    </router-link>
                    <!--tooltip v-if="!preview" v-bind:value="$t('helper.report_comment')">
                        <i class="material-icons"
                           @click="$emit('report', comment)">report</i>
                    </tooltiped-->
                    <span v-if="!preview"
                          class="comment-count right" @click="collapse()">
                        {{ $tc('comment.nb_answer', comment.comments.length) }}
                        <span v-if="comment.comments.length > 0" v-bind:class="{ arrow: true, 'to-down': !collapsed, 'to-right': collapsed }"></span>
                    </span>
                    <div v-if="$listeners.close" class="close" @click="$emit('close')">×</div>
                </label>
                <div class="comment-body">
                    <div class="selection"
                         @click="$emit('highlight-me', comment)">
                        <div v-html="highlightedSelection"></div>
                    </div>

                    <div v-if="comment.reformulation" class="reformulation-wrapper">
                        <i class="material-icons">hearing</i>
                        <div class="reformulation"
                             v-html="highlightedReformulation"></div>
                        <tooltiped v-if="!preview && comment.debate.idefixLink && comment.tags"
                                   v-bind:value="comment.tags.length > 0 ? $t('tags_edition') : $t('no_tags_yet')">
                            <span v-bind:class="{tags: true, disabled: comment.tags.length === 0}"
                                  @click="(comment.tags.length > 0 || $root.user.is('ADMIN')) ? $emit('tag-edition', comment) : ''">
                                <i class="material-icons">local_offer</i>
                            </span>
                        </tooltiped>
                    </div>

                    <div class="argumentation-wrapper">
                        <i class="material-icons" >chat_bubble_outline</i>
                        <div class="argumentation"
                             @mouseup="$emit('selection-end', comment)"
                             v-html="highlightedArgumentation"></div>
                        <bullets-container
                            v-if="!preview"
                            @over-bullet="$emit('select-me', $event)"
                            @click-bullet="$emit('focus-me', $event)"
                            v-bind:displayable-comments="displayableComments"
                            v-bind:comments="comment.comments">
                        </bullets-container>
                    </div>
                </div>
            </div>
            <div v-else v-bind:style="'height: '+height+'px'"></div>
        </div>

        <div v-if="!preview" class="answers-container">
            <transition @before-leave="answersHeight = $refs.answers.scrollHeight"
                         @after-enter="answersHeight = 0">
                <div ref="answers"
                     v-show="!collapsed"
                     class="answers"
                     v-bind:style="'marginTop: -' + this.answersHeight + 'px'">
                    <comment-widget
                        v-for="child in comment.comments"
                        v-bind:displayable-comments="displayableComments"
                        v-show="displayableComments[child.id]"
                        v-bind:key="child.id"
                        v-bind:comment="child"
                        v-bind:search="search"
                        v-bind:hide-on-scroll="hideOnScroll"
                        @tag-edition="$emit('tag-edition', $event)"
                        @selection-end="$emit('selection-end', $event)"
                        @select-me="$emit('select-me', $event)"
                        @focus-me="$emit('focus-me', $event)"
                        @highlight-me="$emit('highlight-me', $event)"
                        @mounted="$emit('mounted', $event)"
                        @report="$emit('report', $event)"
                        @collapse="$emit('collapse', $event)">
                    </comment-widget>
                </div>
            </transition>
        </div>

    </div>
</template>

<script>
    module.exports = {
        props: ['comment', 'displayableComments', 'search', 'preview', 'hideOnScroll'],
        data: function () {
            // Only one observer for all comments
            if (this.hideOnScroll && typeof Vue.prototype.$commentIntersectionObserver === 'undefined') {
                Vue.prototype.$commentIntersectionObserver = new IntersectionObserver(function (entries) {
                    entries.forEach(e => {
                        if (e.isIntersecting === true) {
                            this.$refs[e.target.id].isVisible = true;
                        } else {
                            this.$refs[e.target.id].isVisible = false;
                        }
                    });
                }, {threshold: [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0],
                    rootMargin: '100px',
                    delay: 100});
                Vue.prototype.$commentIntersectionObserver.$refs = [];
            }
            return {
                collapsed: false,
                answersHeight: 0,
                height: 0,
                isVisible: true,
                observer: this.$commentIntersectionObserver
            };
        },
        computed: {
            highlightedSelection() {
                return this.highlight(this.comment.selection);
            },
            highlightedReformulation() {
                return this.highlight(this.escapeHtml(this.comment.reformulation));
            },
            highlightedArgumentation() {
                return this.highlight(this.escapeHtml(this.comment.argumentation));
            }
        },
        mounted() {
            window.addEventListener('beforeprint', this.display);
            this.height = this.$refs.innerComment.offsetHeight;
            if (this.hideOnScroll) {
                this.observer.$refs['comment_' + this.comment.id] = this;
                this.observer.observe(this.$refs.comment);
            }
            this.$emit('mounted', this.comment);
        },
        methods: {
            collapse(value = !this.collapsed) {
                this.collapsed = value;
                let transitionDuration = getComputedStyle(this.$refs.answers).transitionDuration.slice(0, -1) * 1000;
                this.$emit("collapse", transitionDuration);
            },
            display() {
                this.isVisible = true;
            },
            highlight(value) {
                if (this.search && this.search.length > 0) {
                    this.search = this.escapeHtml(this.search.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&'));
                    let re = new RegExp("(" + this.search + ")", "ig");
                    return value.replace(re, "<mark>$1</mark>");
                }
                return value;
            },
            escapeHtml(unsafe) {
                return unsafe
                        .replace(/&/g, "&amp;")
                        .replace(/</g, "&lt;")
                        .replace(/>/g, "&gt;")
                        .replace(/"/g, "&quot;")
                        .replace(/'/g, "&#039;");
            }
        },
        beforeDestroy() {
            if (this.hideOnScroll) {
                this.observer.disconnect();
            }
            window.removeEventListener('beforeprint', this.display);
        },
        components: {
            'comment-widget': vueLoader('components/widgets/comment'),
            'bullets-container': vueLoader('components/widgets/bulletsContainer')
        }
    };
</script>
