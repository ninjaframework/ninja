/**
 * Copyright (C) 2015 Fizzed, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.rocker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import javax.inject.Singleton;
import ninja.Router;
import ninja.i18n.Lang;
import ninja.i18n.Messages;
import ninja.utils.NinjaProperties;
import org.ocpsoft.prettytime.PrettyTime;

/**
 *
 * @author Fizzed, Inc (http://fizzed.com)
 * @author joelauer (http://twitter.com/jjlauer)
 */

@Singleton
public class NinjaRockerContextImpl implements NinjaRockerContext {
    
    private final Router router;
    private final Messages messages;
    private final Provider<Lang> langProvider;
    private final NinjaProperties ninjaProperties;
    private final PrettyTime prettyTime;
    
    @Inject
    public NinjaRockerContextImpl(Router router,
                                    Messages messages,
                                    Provider<Lang> langProvider,
                                    NinjaProperties ninjaProperties,
                                    PrettyTime prettyTime) {
        this.router = router;
        this.messages = messages;
        this.langProvider = langProvider;
        this.ninjaProperties = ninjaProperties;
        this.prettyTime = prettyTime;
    }

    @Override
    public Router getRouter() {
        return this.router;
    }

    @Override
    public Messages getMessages() {
        return this.messages;
    }

    @Override
    public Provider<Lang> getLangProvider() {
        return this.langProvider;
    }

    @Override
    public NinjaProperties getNinjaProperties() {
        return ninjaProperties;
    }

    @Override
    public PrettyTime getPrettyTime() {
        return prettyTime;
    }
    
}
