<template>
    <base-layout id="createDebate">
        <template v-slot:title>
            <h1>Ouvrir un débat</h1>
        </template>

        <template v-slot:right>
        </template>

        <tabs-layout
            class="steps"
            ref="mainTabs"
            v-bind:tabs="[
            {label:$t('with_dots')},
            {label:$t('on_the_doc')},
            {label:$t('recapitulative'), disabled: !debate.document}
            ]"
            @change-tab="scrollTop()">

            <template v-slot:tab.1.header>
                <documented v-bind:value="$t('documentation.create_debate_with')">
                    <img v-if="debate.teams.length === 0" src="assets/img/Aren-icones-dispo-01.svg"/>
                    <img v-else src="assets/img/Aren-icones-clic-01.svg"/>
                </documented>
                <br/>
                <label class="center">{{ $t('with_dots') }}</label>
            </template>

            <template v-slot:tab.2.header>
                <documented v-bind:value="$t('documentation.create_debate_document')">
                    <img v-if="!debate.document" src="assets/img/Aren-icones-dispo-02.svg"/>
                    <img v-else src="assets/img/Aren-icones-clic-02.svg"/>
                </documented>
                <br/>
                <label class="center">{{ $t('on_the_doc') }}</label>
            </template>

            <template v-slot:tab.3.header>
                <documented v-bind:value="$t('documentation.create_debate_recap')">
                    <img src="assets/img/Aren-icones-dispo-03.svg"/>
                </documented>
                <br/>
                <label class="center">{{ $t('recapitulative') }}</label>
            </template>


            <template v-slot:tab.1>
                <div class="container">
                    <contirbutors-widget>
                        <template v-slot:team.side.actions="{ team }">
                            <toggle-action-button
                                v-bind:checked="debate.teams.includes( team )"
                                @toggle="toggleTeam( team )">
                            </toggle-action-button>
                        </template>

                        <template v-slot:user.side.actions="{ user }">
                            <toggle-action-button
                                v-bind:checked="debate.guests.includes( user )"
                                @toggle="toggleGuest( user )">
                            </toggle-action-button>
                        </template>
                    </contirbutors-widget>
                </div>
            </template>

            <template v-slot:tab.2>
                <div class="container">
                    <documents-grid v-bind:categories="categories">
                        <template v-slot:side.actions="{ document }">
                            <toggle-action-button
                                v-bind:checked="debate.document === document"
                                @toggle="$event ? debate.document = document : debate.document = undefined">
                            </toggle-action-button>
                        </template>
                    </documents-grid>
                </div></template>

            <template v-slot:tab.3>
                <div class="container">

                    <div class="row" v-if="debate.document">
                        <template v-if="debate.teams.length !== 0 || debate.guests.length !== 0">
                            <div class="col s4">
                                <ul class="collection with-header">
                                    <li class="collection-header">
                                        <h2 class="valign-wrapper"><i class="material-icons">group</i>{{ $t('teams') }}</h2>
                                    </li>
                                    <li v-for="team in debate.teams" v-bind:key="team.id" class="collection-item">
                                        <span>
                                            <span v-if="team.institution.id !== 0"> {{ team.institution.type }} {{ team.institution.name }} - </span>{{ team.name }}
                                        </span>
                                        <i class="right close material-icons"
                                           @click="debate.teams.remove(team)">delete_forever</i>
                                    </li>
                                </ul>
                            </div>

                            <div class="col s4">
                                <ul class="collection with-header">
                                    <li class="collection-header">
                                        <h2 class="valign-wrapper"><i class="material-icons">person</i>{{ $t('guests') }}</h2>
                                    </li>
                                    <li v-for="user in debate.guests" v-bind:key="user.id" class="collection-item">
                                        <span>
                                            {{ user.fullName() }} - {{ user.atuthority }}
                                        </span>
                                        <i class="right close material-icons"
                                           @click="debate.guests.remove(user)">delete_forever</i>
                                    </li>
                                </ul>
                            </div>
                        </template>
                        <template v-else>
                            <div class="col s8">
                                <ul class="collection with-header">
                                    <li class="collection-header">
                                        <h2 class="valign-wrapper"><i class="material-icons">group</i>{{ $t('public_debate') }}<i class="material-icons">person</i></h2>
                                    </li>
                                    <li class="collection-item">
                                        {{ $t('helper.public_debate') }}
                                    </li>
                                </ul>
                            </div>
                        </template>

                        <div class="col s4">
                            <ul class="collection with-header">
                                <li class="collection-header">
                                    <h2 class="valign-wrapper"><i class="material-icons">description</i>{{ $t('document') }}</h2>
                                </li>
                                <li class="collection-item">
                                    {{ debate.document.category.name }} - {{ debate.document.name }}
                                </li>
                            </ul>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col s4">
                            <ul id="advancedOptions" class="collection with-header">
                                <li class="collection-header"  @click="toggleAdvancedOptions()">
                                    <h2 class="valign-wrapper">
                                        <i class="material-icons">settings_applications</i>{{ $t('advanced_options') }}
                                        <span v-bind:class="{ arrow: true, 'to-down': displayAdvancedOptions, 'to-right': !displayAdvancedOptions }"></span>
                                    </h2>
                                </li>
                                <li id="advancedOptionsBody">
                                    <ul ref="advancedOptions" v-bind:style="'marginTop: ' + (-46 * 4) + 'px;'">
                                        <li class="collection-item">
                                            <tooltiped tag="label" v-bind:value="$t('helper.reformulation_assist')">
                                                <label>
                                                    <input type="checkbox" v-model="debate.reformulationCheck" />
                                                    <span>{{ $t('reformulation_assist') }}</span>
                                                </label>
                                            </tooltiped>
                                        </li>
                                        <li class="collection-item">
                                            <tooltiped tag="label" v-bind:value="$t('helper.idefix_link')">
                                                <label>
                                                    <input type="checkbox" v-model="debate.idfixLink"/>
                                                    <span>{{ $t('idefix_link') }}</span>
                                                </label>
                                            </tooltiped>
                                        </li>
                                        <li class="collection-item">
                                            <tooltiped tag="label" v-bind:value="$t('helper.with_hypostases')">
                                                <label>
                                                    <input type="checkbox" v-model="debate.withHypostases"/>
                                                    <span>{{ $t('with_hypostases') }}</span>
                                                </label>
                                            </tooltiped>
                                        </li>
                                    </ul>
                                </li>
                            </ul>
                        </div>
                        <div class="col s4">
                        </div>
                        <div class="col s4">
                            <span class="right waves-effect waves-light btn"
                                  @click="createDebate()">Lancer le débat</span>
                        </div>
                    </div>
                </div>
            </template>
        </tabs-layout>

        <template v-slot:addons>
        </template>
    </base-layout>
</template>

<script>
    module.exports = {
        data( ) {
            return {
                debate: new Debate( ),
                categories: ArenService.Store.Category,
                institution: new Institution( ),
                search: "",
                step: 0,
                displayAdvancedOptions: false
            };
        },
        created( ) {
            if (!this.$root.user.is('MODO')) {
                this.$router.push("/404");
                return;
            }
            this.fetchData( );
            this.debate.reformulationCheck = true;
        },
        methods: {
            fetchData( ) {
                ArenService.Documents.getAll();
            },
            reinit() {
                this.debate = new Debate( );
                this.debate.reformulationCheck = true;
                this.$refs.mainTabs.activeIndex = 0;
            },
            institutionChanged(institution) {
                this.institution = institution;
            },
            toggleTeam(team) {
                if (!this.debate.teams.includes(team)) {
                    this.debate.teams.push(team);
                } else {
                    this.debate.teams.remove(team);
                }
            },
            toggleGuest(user) {
                if (!this.debate.guests.includes(user)) {
                    this.debate.guests.push(user);
                } else {
                    this.debate.guests.remove(user);
                }
            },
            toggleAdvancedOptions(value) {
                value = typeof value === "undefined" ? !this.displayAdvancedOptions : value;
                if (value) {
                    this.$refs.advancedOptions.style.marginTop = 0;
                } else {
                    this.$refs.advancedOptions.style.marginTop = (-46 * 4) + "px";
                }
                this.displayAdvancedOptions = value;
            },
            createDebate( ) {
                if (this.debate.teams.length === 0 && this.debate.guests.length === 0) {
                    this.debate.openPublic = true;
                }
                ArenService.Debates.create({
                    data: this.debate,
                    onSuccess: (debate) => {
                        this.debate = debate;
                        this.$confirm({
                            title: this.$t("creation_success"),
                            message: this.$t('helper.debate_created',
                                    {documentName: this.debate.document.name, categoryName: this.debate.document.category.name}),
                            cancelLabel: this.$t('no'),
                            validateLabel: this.$t('yes'),
                            callback: (returnValue) => {
                                if (returnValue) {
                                    this.$router.push('/debates/' + this.debate.id);
                                } else {
                                    this.reinit();
                                }
                            }
                        });
                    }
                });
            },
            scrollTop() {
                document.documentElement.scrollTop = 0;
            }
        },
        components: {
            'contirbutors-widget': vueLoader('components/widgets/contributors'),
            'documents-grid': vueLoader('components/grids/documentsGrid')
        }
    };
</script>
