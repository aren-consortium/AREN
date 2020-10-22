<template>
    <div class="scroll-content" id="tagUpdate">

        <div class="container">

            <div id="header">
                <h1>{{ $t('update_tags') }}</h1>
            </div>

            <div class="container row">
                <div class="row">
                    <button class="right btn waves-effect waves-light"
                            v-bind:disabled="progress > 0"
                            @click="startUpdate()">
                        {{ progress !== false ? $t('running') : $t('start') }}
                    </button>
                    <div class="progress col s8">
                        <div class="determinate" v-bind:style="'width: ' + progress + '%'"></div>
                    </div>
                    <div v-if="progress > 0" class="col s2">{{ Math.round(progress*100)/100 + '%' }}</div>
                </div>
                <div v-if="progress > 0" v-html="$t('helper.update_tags')"></div>
            </div>
        </div>
    </div>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                progress: false
            };
        },
        methods: {
            startUpdate( ) {
                this.progress = 0;
                ArenService.Comments.updateAllTags({
                    onSuccess: (response) => {
                        this.$confirm({
                            title: this.$t('update_success'),
                            message: this.$t('helper.tag_update_success'),
                            isInfo: true
                        });
                        this.progress = 0;
                    },
                    onError: () => {
                        this.$confirm({
                            title: this.$t('error.Insertion error'),
                            message: this.$t('error.tag_update'),
                            isInfo: true
                        });
                        this.progress = 0;
                    },
                    onProgress: (response) => {
                        this.progress = response * 100;
                    },
                    loading: false
                });
            }
        }
    };
</script>