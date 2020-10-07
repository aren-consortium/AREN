<template>
    <modal-layout v-bind:movable="true">

        <template v-slot:title>{{ $t('comment.add_argument') }}</template>

        <div class="selection">
            <div v-html="comment.selection" ref="selection"></div>
        </div>

        <label>{{ $t('i_am') }}</label>
        <div id="opinion">
            <label>
                <input name="opinion" type="radio" v-model="comment.opinion" value="AGAINST"/>
                <span class="btn against">{{ $t("comment.against") }}</span>
            </label>
            <label>
                <input name="opinion" type="radio" v-model="comment.opinion" value="FOR"/>
                <span class="btn for">{{ $t("comment.for") }}</span>
            </label>
            <label>
                <input name="opinion" type="radio" v-model="comment.opinion" value="NEUTRAL"/>
                <span class="btn neutral">{{ $t("comment.neutral") }}</span>
            </label>
        </div>

        <template v-if="comment.opinion">
            <text-input
                v-if="comment.opinion !== 'NEUTRAL'"
                v-model="comment.reformulation"
                type="textarea"
                v-bind:label="$t('comment.reformulation')"
                @input="checkReformulation()">

                <div v-if="comment.debate.reformulationCheck" id="reformulationCheck" v-bind:class="{ loading: comment.reformulation && comment.reformulation.length > 0 && scalar === -1 }">
                    <svg width="32px" height="32px"
                         xmlns="http://www.w3.org/2000/svg">

                        <ellipse class="eye"   cx="7"   cy="12"  rx="5.5"  ry="6"/>
                        <ellipse class="eye"   cx="25"  cy="12"  rx="5.5"  ry="6"/>
                        <circle  class="pupil" cx="7"   cy="12"  r="1.5"         />
                        <circle  class="pupil" cx="25"  cy="12"  r="1.5"         />

                        <template v-if="scalar >= 0">
                            <template v-if="scalar <= 0.05">
                                <path class="eyelid" d="M 7  6 A 5.5 6 0 0 0 1.5664062 11.119141 L 12.435547 11.119141 A 5.5 6 0 0 0 7  6 z " />
                                <path class="eyelid" d="M 25 6 A 5.5 6 0 0 0 19.566406 11.119141 L 30.435547 11.119141 A 5.5 6 0 0 0 25 6 z " />
                            </template>
                            <template v-else-if="scalar <= 0.25">
                                <path class="mouth" d="m 24.52,21.65 c -0.79,1.08 -3.36,3.4 -7.5,3.4"/>
                                <path class="line"  d="m -4.5,13.5 c 4,0 12,-3 17,-6 7.9,-4.74 14,-4 14,-4"/>
                            </template>
                            <template v-else-if="scalar <= 0.99">
                                <path class="mouth" d="M 8.4628906,21.5 C 9.9255753,23.649908 12.830847,24.999027 16,25 c 3.167748,-0.0021 6.071167,-1.351043 7.533203,-3.5"/>
                            </template>
                            <template v-else-if="scalar <= 1.21">
                                <path class="eyelid" d="M 12.462891,12.664062 2.0546875,14.615234 A 5.5,6 0 0 0 7,18 5.5,6 0 0 0 12.462891,12.664062 Z"/>
                                <path class="eyelid" d="m 25,6 a 5.5,6 0 0 0 -5.464844,5.337891 L 29.945312,9.384766 A 5.5,6 0 0 0 25,6 Z" />
                                <path class="line"   d="M 0,15 32,9"/>
                            </template>
                        </template>
                    </svg>
                    <span v-if="!comment.reformulation || comment.reformulation.length === 0 || scalar === undefined">{{ $t('argumentation_check.waiting') }}</span>
                    <span v-else-if="scalar < 0"    >{{ $t('argumentation_check.loading') }}</span>
                    <span v-else-if="scalar <= 0.05">{{ $t('argumentation_check.off_topic') }}</span>
                    <span v-else-if="scalar <= 0.25">{{ $t('argumentation_check.close') }}</span>
                    <span v-else-if="scalar <= 0.99">{{ $t('argumentation_check.nice') }}</span>
                    <span v-else-if="scalar <= 1.05">{{ $t('argumentation_check.identical') }}</span>
                </div>
            </text-input>

            <text-input
                id="argumentation"
                v-model="comment.argumentation"
                type="textarea"
                v-bind:label="$t('comment.argumentation')">
            </text-input>

            <div v-if="comment.debate.withHypostases">
                <label>
                    <input name="group1" type="radio" v-bind:value="false" v-model="isArgument" />
                    <span>{{ $t('comment.is_opinion') }}</span>
                </label>
                <label>
                    <input name="group1" type="radio" v-bind:value="true" v-model="isArgument" />
                    <span>{{ $t('comment.is_argument') }}</span>
                </label>
                <div v-if="isArgument" id="hypostase">
                    <label>{{ $t('comment.select_hypostases') }}</label>
                    <toggle-action-button
                        class="right"
                        v-bind:off-label="$t('comment.expert')"
                        v-model="expertMode">
                    </toggle-action-button>
                    <div v-bind:class="{ list: true, expert: expertMode }">
                        <div v-for="group in hypostases">
                            <template v-if="expertMode">
                                <template v-for="hypostase in group" >
                                    <input v-bind:id="hypostase"
                                           type="checkbox"
                                           v-model="comment.hypostases"
                                           v-bind:value="hypostase"/>
                                    <label v-bind:for="hypostase" class="btn">
                                        {{ $t('hypostase.'+hypostase.toLowerCase()) }}
                                    </label>
                                </template>
                            </template>
                            <template v-else>
                                <input v-bind:id="group[0]"
                                       type="checkbox"
                                       v-model="comment.hypostases"
                                       v-bind:value="group[0]"/>
                                <label v-bind:for="group[0]" class="btn">
                                    {{ $t('hypostase.'+group[0].toLowerCase()) }}
                                </label>
                            </template>
                        </div>
                    </div>
                </div>
            </div>

        </template>

        <template v-slot:footer>
            <button @click="close(false)" class="waves-effect waves-green btn-flat">{{ $t('cancel') }}</button>
            <button @click="close(true)"
                     v-bind:disabled="!isFilled"
                     class="waves-effect waves-green btn-flat">{{ $t('validate') }}</button>
        </template>

    </modal-layout>
</template>

<script>
    module.exports = {
        mixins: [VueModal],
        props: ['comment'],
        data: function () {
            return {
                scalar: -1,
                inputTimeout: 0,
                hypostases: Hypostase,
                isArgument: false,
                expertMode: false

            };
        },
        computed: {
            isFilled() {
                return this.comment.opinion && this.comment.argumentation && (this.comment.opinion === 'NEUTRAL' || this.comment.reformulation.trim().length > 0);
            }
        },
        watch: {
            isArgument(newVal, oldVal) {
                if (!newVal) {
                    this.comment.hypostases = [];
                }
            },
            expertMode(newVal, oldVal) {
                this.comment.hypostases = [];
            }
        },
        methods: {
            afterOpen() {
                this.scalar = -1;
                this.isArgument = false;
                this.expertMode = false;
            },
            checkReformulation() {
                this.scalar = -1;
                clearTimeout(this.inputTimeout);
                this.inputTimeout = setTimeout(this.getScalar, 2000);
            },
            getScalar() {
                if (this.comment && this.comment.reformulation.length > 0) {
                    ArenService.Comments.getScalar({
                        data: {selection: this.$refs.selection.innerText, reformulation: this.comment.reformulation},
                        onSuccess: e => this.scalar = e.result,
                        loading: false
                    });
                }
            }
        }
    };
</script>