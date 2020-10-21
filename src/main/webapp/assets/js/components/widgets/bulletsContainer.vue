<template>
    <div class="bullets-container">
        <div v-for="(bullets, y) in bulletsByY"
             v-bind:style="'top: ' + y + 'px'"
             class="bullets-box">
            <template v-if="bullets.length <= 5">
                <div v-for="bullet in bullets"
                     v-bind:class="'bullet ' + bullet.comment.opinion.toLowerCase()"
                     @mouseover="$emit('over-bullet', bullet.comment)"
                     @click="$emit('click-bullet', bullet.comment)">
                </div>
            </template>

            <div v-else v-bind:class="{ 'bullets-drop-down': true, 'z-depth-2': true, 'for': onlyFor(), 'against': onlyAgainst(), 'neutral': onlyNeutral() }"
                 v-bind:style="{ background: gradient(bullets) }">
                <div class="label">{{ bullets.length }}</div>
                <div class="body"
                     v-bind:style="'height: ' + bullets.length*10 + 'px'">
                    <div v-for="bullet in bullets"
                         v-bind:class="'bullet ' + bullet.comment.opinion.toLowerCase()"
                         @mouseover="$emit('over-bullet', bullet.comment)"
                         @click="$emit('click-bullet', bullet.comment)">
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    module.exports = {
        props: ['comments', 'displayableComments'],
        data() {
            return {
                source: false
            }
        },
        mounted() {
            this.source = this.$el.previousElementSibling;
        },
        computed: {
            bullets( ) {
                if (this.source) {
                    return this.comments
                            .filter(comment => this.displayableComments[comment.id])
                            .map(this.getBulletFromComment)
                            .sort((a, b) => (a.y > b.y) ? 1 : (a.y < b.y) ? -1 : (a.x >= b.x) ? 1 : -1);
                }
                return [];
            },
            bulletsByY( ) {
                let bulletsByY = {};
                let len = this.bullets.length;
                let lastY = -10;
                for (let i = 0; i < len; i++) {
                    if (this.bullets[i].y < lastY - 5 || this.bullets[i].y > lastY + 5) {
                        lastY = this.bullets[i].y;
                    }
                    if (!bulletsByY[lastY]) {
                        bulletsByY[lastY] = [];
                    }
                    bulletsByY[lastY].push(this.bullets[i]);
                }
                return bulletsByY;
            }
        },
        methods: {
            getBulletFromComment(comment) {
                let range = new Range( );
                try {
                    range.setStart(comment.startContainer ? this.source.childPathSelector(comment.startContainer) : this.source.firstChild, comment.startOffset);
                } catch (error) {

                    range.setStart(comment.startContainer ? this.source.childPathSelector(comment.startContainer) : this.source.firstChild, 0);
                }
                let x = Math.round(range.getBoundingClientRect( ).x);
                let y = Math.round(range.getBoundingClientRect( ).y - this.source.getBoundingClientRect( ).y);
                return {x, y, comment};
            },
            onlyFor() {
                return this.bullets.filter(bullet => bullet.comment.opinion === Opinion.FOR).length === this.bullets.length;
            },
            onlyAgainst() {
                return this.bullets.filter(bullet => bullet.comment.opinion === Opinion.AGAINST).length === this.bullets.length;
            },
            onlyNeutral() {
                return this.bullets.filter(bullet => bullet.comment.opinion === Opinion.NEUTRAL).length === this.bullets.length;
            },
            gradient(bullets) {
                let len = bullets.length;
                let blue = 0, red = 0;
                for (let i = 0; i < len; i++) {
                    if (bullets[i].comment.opinion == "FOR") {
                        blue++
                    } else if (bullets[i].comment.opinion == "AGAINST") {
                        red++;
                    }
                }
                let grey = len - blue - red;
                blue = blue > 0 ? Math.max(20, (blue / len) * 100) : 0;
                red = grey > 0 ? Math.min(80, blue + ((red / len) * 100)) : 100;

                return "linear-gradient(110deg, " + blueColor + " " + blue + "%, transparent " + blue + "%), \
                        linear-gradient(110deg, " + redColor + " " + red + "%, " + greyColor + " " + red + "%)";
            }
        }
    };
</script>